package org.logl.logl;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * A {@link Supplier} for a {@link PrintWriter} that writes to an sequence of output files, rotating to a new file
 * whenever the current file exceeds a given size or time period.
 */
public final class RotatingFilePrintWriterSupplier implements Supplier<PrintWriter>, Closeable {

  private static final Writer NULL_WRITER = new NullWriter();

  private final Path outputFile;
  private final FileRotationStrategy rotationStrategy;
  private final int maxArchives;
  private final FileRotationListener rotationListener;
  private final Executor rotationExecutor;

  private final ReadWriteLock logFileLock = new ReentrantReadWriteLock(true);
  private final PrintWriter writer = new PrintWriter(new LockingWriter(), false);
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private final AtomicBoolean rotating = new AtomicBoolean(false);
  private final AtomicLong written = new AtomicLong(0);
  private Writer out = NULL_WRITER;

  /**
   * Create a {@code PrintWriter} supplier.
   *
   * @param outputFile The file to write the latest output to.
   * @param rotationStrategy The strategy for rotating this file away.
   * @param maxArchives The maximum number of rotated archives to keep.
   * @throws IOException If the file could not be opened for writing.
   */
  public RotatingFilePrintWriterSupplier(Path outputFile, FileRotationStrategy rotationStrategy, int maxArchives)
      throws IOException {
    this(outputFile, rotationStrategy, maxArchives, new NoOpListener(), Runnable::run);
  }

  /**
   * Create a {@code PrintWriter} supplier.
   *
   * @param outputFile The file to write the latest output to.
   * @param rotationStrategy The strategy for rotating this file away.
   * @param maxArchives The maximum number of rotated archives to keep.
   * @param rotationListener A listener for callbacks related to rotation.
   * @throws IOException If the file could not be opened for writing.
   */
  public RotatingFilePrintWriterSupplier(
      Path outputFile,
      FileRotationStrategy rotationStrategy,
      int maxArchives,
      FileRotationListener rotationListener) throws IOException {
    this(outputFile, rotationStrategy, maxArchives, rotationListener, Runnable::run);
  }

  /**
   * Create a {@code PrintWriter} supplier.
   *
   * @param outputFile The file to write the latest output to.
   * @param rotationStrategy The strategy for rotating this file away.
   * @param maxArchives The maximum number of rotated archives to keep.
   * @param rotationExecutor A {@link Executor} that will be used for running rotations.
   * @throws IOException If the file could not be opened for writing.
   */
  public RotatingFilePrintWriterSupplier(
      Path outputFile,
      FileRotationStrategy rotationStrategy,
      int maxArchives,
      Executor rotationExecutor) throws IOException {
    this(outputFile, rotationStrategy, maxArchives, new NoOpListener(), rotationExecutor);
  }

  /**
   * Create a {@code PrintWriter} supplier.
   *
   * @param outputFile The file to write the latest output to.
   * @param rotationStrategy The strategy for rotating this file away.
   * @param maxArchives The maximum number of rotated archives to keep.
   * @param rotationListener A listener for callbacks related to rotation.
   * @param rotationExecutor A {@link Executor} that will be used for running rotations.
   * @throws IOException If the file could not be opened for writing.
   */
  public RotatingFilePrintWriterSupplier(
      Path outputFile,
      FileRotationStrategy rotationStrategy,
      int maxArchives,
      FileRotationListener rotationListener,
      Executor rotationExecutor) throws IOException {
    this.outputFile = outputFile;
    this.rotationStrategy = rotationStrategy;
    this.maxArchives = maxArchives;
    this.rotationListener = rotationListener;
    this.rotationExecutor = rotationExecutor;
    this.out = openOutputFile(outputFile);
  }

  @Override
  public PrintWriter get() {
    if (!closed.get() && !rotating.get()) {
      if (rotationStrategy.shouldRotate(outputFile, written.get())) {
        rotate();
      }
    }
    return writer;
  }

  @Override
  public void close() throws IOException {
    logFileLock.writeLock().lock();
    try {
      closed.set(true);
      out.close();
    } finally {
      out = NULL_WRITER;
      logFileLock.writeLock().unlock();
    }
  }

  // visible for testing
  void rotate() {
    if (rotating.getAndSet(true)) {
      return;
    }

    StringWriter buffer = new StringWriter();
    PrintWriter bufferWriter = new PrintWriter(buffer);
    Runnable runnable = () -> {
      logFileLock.writeLock().lock();
      try {
        try {
          try {
            out.flush();
            out.close();
          } catch (Exception e) {
            // failed to close
            rotationListener.rotationError(e, bufferWriter);
            return;
          }

          out = NULL_WRITER;
          written.set(0);

          if (Files.exists(outputFile)) {
            try {
              shift();
              Files.move(outputFile, archiveFile(outputFile, 1));
            } catch (Exception e) {
              // failed to shift
              rotationListener.rotationError(e, bufferWriter);
              return;
            }
          }
        } finally {
          if (!closed.get() && out == NULL_WRITER) {
            try {
              out = openOutputFile(outputFile);
            } catch (IOException e) {
              // failed to open output
              rotationListener.rotationError(e, bufferWriter);
            }
          }
        }

        rotationStrategy.rotationCompleted();
        rotationListener.rotationCompleted(bufferWriter);
      } finally {
        try {
          out.append(buffer.getBuffer());
        } catch (IOException e) {
          // ignore
        }
        rotating.set(false);
        logFileLock.writeLock().unlock();
      }
    };

    try {
      rotationExecutor.execute(runnable);
    } catch (Exception e) {
      rotating.set(false);
    }
  }

  /**
   * @return A list of all archives, in order from most recent to oldest.
   */
  public List<Path> archives() {
    List<Path> archives = new ArrayList<>();
    Path archive;
    for (int i = 1; Files.exists(archive = archiveFile(outputFile, i)); ++i) {
      archives.add(archive);
    }
    return archives;
  }

  private void shift() throws IOException {
    for (int i = lastArchiveFileNumber(outputFile); i > 0; --i) {
      Path archive = archiveFile(outputFile, i);
      if (i >= maxArchives) {
        Files.delete(archive);
      } else {
        Files.move(archive, archiveFile(outputFile, i + 1));
      }
    }
  }

  private static Path archiveFile(Path outputFile, int number) {
    return outputFile.resolveSibling(outputFile.getFileName().toString() + '.' + number);
  }

  private static int lastArchiveFileNumber(Path outputFile) {
    int i = 0;
    while (Files.exists(archiveFile(outputFile, i + 1))) {
      i++;
    }
    return i;
  }

  private static Writer openOutputFile(Path file) throws IOException {
    return Files.newBufferedWriter(file, StandardCharsets.UTF_8);
  }

  private static long fileSize(Path file) {
    try {
      return Files.size(file);
    } catch (IOException e) {
      // If we cannot check the filesize, force a rotation.
      return Long.MAX_VALUE;
    }
  }

  private class LockingWriter extends Writer {
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
      logFileLock.readLock().lock();
      try {
        out.write(cbuf, off, len);
      } finally {
        logFileLock.readLock().unlock();
      }
      written.getAndAdd(len);
    }

    @Override
    public void flush() throws IOException {
      logFileLock.readLock().lock();
      try {
        out.flush();
      } finally {
        logFileLock.readLock().unlock();
      }
    }

    @Override
    public void close() {}
  }

  private static class NullWriter extends Writer {
    @Override
    public void write(char[] cbuf, int off, int len) {}

    @Override
    public void flush() {}

    @Override
    public void close() {}
  }

  private static class NoOpListener implements FileRotationListener {
    @Override
    public void rotationCompleted(PrintWriter out) {}

    @Override
    public void rotationError(Exception e, PrintWriter out) {}
  }
}

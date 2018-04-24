package org.logl;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.logl.UnformattedLogger.Builder;

final class UnformattedLoggerImpl implements AdjustableLogger, LevelLogger {

  private final Locale locale;
  private final AtomicReference<Level> level;
  private final boolean autoFlush;
  private final Supplier<PrintWriter> writerSupplier;

  private final LevelLogWriter errorWriter;
  private final LevelLogWriter warnWriter;
  private final LevelLogWriter infoWriter;
  private final LevelLogWriter debugWriter;

  UnformattedLoggerImpl(Builder builder, Supplier<PrintWriter> writerSupplier) {
    this.locale = builder.locale;
    this.level = new AtomicReference<>(builder.level);
    this.autoFlush = builder.autoFlush;
    this.writerSupplier = writerSupplier;

    this.errorWriter = new LevelLogWriter(Level.ERROR, this);
    this.warnWriter = new LevelLogWriter(Level.WARN, this);
    this.infoWriter = new LevelLogWriter(Level.INFO, this);
    this.debugWriter = new LevelLogWriter(Level.DEBUG, this);
  }

  @Override
  public Level getLevel() {
    return this.level.get();
  }

  @Override
  public Level setLevel(Level level) {
    requireNonNull(level);
    return this.level.getAndSet(level);
  }

  @Override
  public boolean isEnabled(Level level) {
    requireNonNull(level);
    if (level == Level.NONE) {
      return false;
    }
    return level.compareTo(this.level.get()) <= 0;
  }

  @Override
  public LogWriter errorWriter() {
    return this.errorWriter;
  }

  @Override
  public LogWriter warnWriter() {
    return this.warnWriter;
  }

  @Override
  public LogWriter infoWriter() {
    return this.infoWriter;
  }

  @Override
  public LogWriter debugWriter() {
    return this.debugWriter;
  }

  @Override
  public void log(Level level, LogMessage message) {
    requireNonNull(message);
    if (level.compareTo(this.level.get()) > 0) {
      return;
    }
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      writeMessage(out, message);
      out.println();
    }
    if (autoFlush) {
      out.flush();
    }
  }

  @Override
  public void log(Level level, CharSequence message) {
    requireNonNull(message);
    if (level.compareTo(this.level.get()) > 0) {
      return;
    }
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      out.println(message);
    }
    if (autoFlush) {
      out.flush();
    }
  }

  @Override
  public void log(Level level, LogMessage message, Throwable cause) {
    if (cause == null) {
      log(level, message);
      return;
    }
    requireNonNull(message);
    if (level.compareTo(this.level.get()) > 0) {
      return;
    }
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      writeMessage(out, message);
      out.println();
      cause.printStackTrace(out);
    }
    if (autoFlush) {
      out.flush();
    }
  }

  @Override
  public void log(Level level, CharSequence message, Throwable cause) {
    if (cause == null) {
      log(level, message);
      return;
    }
    requireNonNull(message);
    if (level.compareTo(this.level.get()) > 0) {
      return;
    }
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      out.println(message);
      cause.printStackTrace(out);
    }
    if (autoFlush) {
      out.flush();
    }
  }

  @Override
  public void logf(Level level, String format, Object... args) {
    requireNonNull(format);
    if (level.compareTo(this.level.get()) > 0) {
      return;
    }
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      out.printf(format, args);
      out.println();
    }
    if (autoFlush) {
      out.flush();
    }
  }

  @Override
  public void batch(Consumer<Logger> fn) {
    requireNonNull(fn);
    List<LogEvent> events = new ArrayList<>(32);
    fn.accept(new BatchLogger(events::add));
    writeEvents(events);
  }

  @Override
  public void batch(Level level, Consumer<LogWriter> fn) {
    requireNonNull(fn);
    List<LogEvent> events = new ArrayList<>(32);
    fn.accept(new BatchLogWriter(level, events::add));
    writeEvents(events);
  }

  private static final class LogEvent {
    final Level level;
    final CharSequence formattedMessage;
    final LogMessage message;
    final Throwable cause;

    LogEvent(Level level, CharSequence formattedMessage, Throwable cause) {
      this.level = level;
      this.formattedMessage = formattedMessage;
      this.message = null;
      this.cause = cause;
    }

    LogEvent(Level level, LogMessage message, Throwable cause) {
      this.level = level;
      this.formattedMessage = null;
      this.message = message;
      this.cause = cause;
    }
  }

  private static final class BatchLogger implements Logger {
    private final LogWriter errorWriter;
    private final LogWriter warnWriter;
    private final LogWriter infoWriter;
    private final LogWriter debugWriter;

    BatchLogger(Consumer<LogEvent> eventConsumer) {
      this.errorWriter =
          isEnabled(Level.ERROR) ? new BatchLogWriter(Level.ERROR, eventConsumer) : NullLogWriter.instance();
      this.warnWriter =
          isEnabled(Level.WARN) ? new BatchLogWriter(Level.WARN, eventConsumer) : NullLogWriter.instance();
      this.infoWriter =
          isEnabled(Level.INFO) ? new BatchLogWriter(Level.INFO, eventConsumer) : NullLogWriter.instance();
      this.debugWriter =
          isEnabled(Level.DEBUG) ? new BatchLogWriter(Level.DEBUG, eventConsumer) : NullLogWriter.instance();
    }

    @Override
    public Level getLevel() {
      return Level.DEBUG;
    }

    @Override
    public boolean isEnabled(Level level) {
      requireNonNull(level);
      return true;
    }

    @Override
    public LogWriter errorWriter() {
      return this.errorWriter;
    }

    @Override
    public LogWriter warnWriter() {
      return this.warnWriter;
    }

    @Override
    public LogWriter infoWriter() {
      return this.infoWriter;
    }

    @Override
    public LogWriter debugWriter() {
      return this.debugWriter;
    }

    @Override
    public void batch(Consumer<Logger> fn) {
      requireNonNull(fn);
      fn.accept(this);
    }
  }

  private static final class BatchLogWriter implements LogWriter {
    private final Level level;
    private final Consumer<LogEvent> eventConsumer;

    BatchLogWriter(Level level, Consumer<LogEvent> eventConsumer) {
      this.level = level;
      this.eventConsumer = eventConsumer;
    }

    @Override
    public void log(LogMessage message) {
      requireNonNull(message);
      eventConsumer.accept(new LogEvent(level, message, null));
    }

    @Override
    public void log(CharSequence message) {
      requireNonNull(message);
      eventConsumer.accept(new LogEvent(level, message, null));
    }

    @Override
    public void log(LogMessage message, Throwable cause) {
      requireNonNull(message);
      eventConsumer.accept(new LogEvent(level, message, cause));
    }

    @Override
    public void log(CharSequence message, Throwable cause) {
      requireNonNull(message);
      eventConsumer.accept(new LogEvent(level, message, cause));
    }

    @Override
    public void batch(Consumer<LogWriter> fn) {
      fn.accept(this);
    }
  }

  private void writeEvents(Collection<LogEvent> logEvents) {
    if (logEvents.isEmpty()) {
      return;
    }
    Level currentLevel = this.level.get();
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      for (LogEvent logEvent : logEvents) {
        if (logEvent.level.compareTo(currentLevel) > 0) {
          continue;
        }
        LogMessage message = logEvent.message;
        if (message != null) {
          writeMessage(out, message);
          out.println();
        } else {
          out.println(logEvent.formattedMessage);
        }
        Throwable cause = logEvent.cause;
        if (cause != null) {
          cause.printStackTrace(out);
        }
      }
    }
    if (autoFlush) {
      out.flush();
    }
  }

  private void writeMessage(PrintWriter out, LogMessage message) {
    try {
      message.appendTo(locale, out);
    } catch (IOException ex) {
      // PrintWriter does not throw this exception
      throw new RuntimeException("unexpected exception", ex);
    }
  }
}

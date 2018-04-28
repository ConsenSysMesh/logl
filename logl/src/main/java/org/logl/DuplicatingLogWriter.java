package org.logl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class DuplicatingLogWriter implements LogWriter {
  private final Level level;
  private final DuplicatingLogger logger;
  private final List<LogWriter> logWriters;

  DuplicatingLogWriter(Level level, DuplicatingLogger logger, List<LogWriter> logWriters) {
    this.level = level;
    this.logger = logger;
    this.logWriters = new CopyOnWriteArrayList<>(logWriters);
  }

  boolean remove(LogWriter logWriter) {
    return logWriters.remove(logWriter);
  }

  @Override
  public void log(LogMessage message) {
    if (logger.checkLevel(level)) {
      for (LogWriter logWriter : logWriters) {
        logWriter.log(message);
      }
    }
  }

  @Override
  public void log(CharSequence message) {
    if (logger.checkLevel(level)) {
      for (LogWriter logWriter : logWriters) {
        logWriter.log(message);
      }
    }
  }

  @Override
  public void log(Supplier<? extends CharSequence> messageSupplier) {
    if (logger.checkLevel(level)) {
      CharSequence message = messageSupplier.get();
      for (LogWriter logWriter : logWriters) {
        logWriter.log(message);
      }
    }
  }

  @Override
  public void log(LogMessage message, Throwable cause) {
    if (logger.checkLevel(level)) {
      for (LogWriter logWriter : logWriters) {
        logWriter.log(message, cause);
      }
    }
  }

  @Override
  public void log(CharSequence message, Throwable cause) {
    if (logger.checkLevel(level)) {
      for (LogWriter logWriter : logWriters) {
        logWriter.log(message, cause);
      }
    }
  }

  @Override
  public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    if (logger.checkLevel(level)) {
      CharSequence message = messageSupplier.get();
      for (LogWriter logWriter : logWriters) {
        logWriter.log(message, cause);
      }
    }
  }

  @Override
  public void log(String pattern, Object... args) {
    if (logger.checkLevel(level)) {
      for (LogWriter logWriter : logWriters) {
        logWriter.log(pattern, args);
      }
    }
  }

  @Override
  public void logf(String format, Object... args) {
    if (logger.checkLevel(level)) {
      for (LogWriter logWriter : logWriters) {
        logWriter.log(format, args);
      }
    }
  }

  @Override
  public void batch(Consumer<LogWriter> fn) {
    batch(0, new ArrayList<>(logWriters.size()), fn);
  }

  private void batch(int index, ArrayList<LogWriter> batchLoggers, Consumer<LogWriter> fn) {
    if (index < logWriters.size()) {
      LogWriter logWriter = logWriters.get(index);
      logWriter.batch(bulkLogger -> {
        batchLoggers.add(bulkLogger);
        batch(index + 1, batchLoggers, fn);
      });
    } else {
      fn.accept(new DuplicatingLogWriter(level, logger, batchLoggers));
    }
  }
}

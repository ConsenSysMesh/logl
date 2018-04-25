package org.logl.slf4j;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Locale;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.logl.Level;
import org.logl.LogMessage;
import org.logl.LogWriter;
import org.logl.Logger;

/**
 * An implementation of a {@link Logger} that delegates logging to a {@link org.slf4j.Logger org.slf4j.Logger}.
 */
public final class Slf4jLogger implements Logger {

  private static final int STRING_BUILDER_CAPACITY = 100;
  private static final ThreadLocal<SoftReference<StringBuilder>> REUSABLE_BUILDER =
      ThreadLocal.withInitial(() -> new SoftReference<>(null));

  private final org.slf4j.Logger slf4jLogger;
  // read lock is for all writes to the log, write lock is for batch writes.
  private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
  private final LogWriter errorWriter;
  private final LogWriter warnWriter;
  private final LogWriter infoWriter;
  private final LogWriter debugWriter;

  /**
   * @param slf4jLogger the SLF4J logger to output to
   */
  public Slf4jLogger(org.slf4j.Logger slf4jLogger) {
    this.slf4jLogger = slf4jLogger;

    this.errorWriter = new LogWriter() {
      @Override
      public void log(LogMessage message) {
        if (slf4jLogger.isErrorEnabled()) {
          StringBuilder builder = stringBuilder();
          try {
            message.appendTo(Locale.getDefault(), builder);
          } catch (IOException e) {
            // not thrown
            throw new RuntimeException(e);
          }
          String msg = builder.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.error(msg);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        if (slf4jLogger.isErrorEnabled()) {
          StringBuilder builder = stringBuilder();
          try {
            message.appendTo(Locale.getDefault(), builder);
          } catch (IOException e) {
            // not thrown
            throw new RuntimeException(e);
          }
          String msg = builder.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.error(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(CharSequence message) {
        if (slf4jLogger.isErrorEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.error(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(CharSequence message, Throwable cause) {
        if (slf4jLogger.isErrorEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.error(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier) {
        if (slf4jLogger.isErrorEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            slf4jLogger.error(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
        if (slf4jLogger.isErrorEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            slf4jLogger.error(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void logf(String format, Object... args) {
        if (slf4jLogger.isErrorEnabled()) {
          lock.readLock().lock();
          try {
            slf4jLogger.error(String.format(format, args));
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void batch(Consumer<LogWriter> fn) {
        if (slf4jLogger.isErrorEnabled()) {
          lock.writeLock().lock();
          try {
            fn.accept(this);
          } finally {
            lock.writeLock().unlock();
          }
        }
      }
    };

    this.warnWriter = new LogWriter() {
      @Override
      public void log(LogMessage message) {
        if (slf4jLogger.isWarnEnabled()) {
          StringBuilder builder = stringBuilder();
          try {
            message.appendTo(Locale.getDefault(), builder);
          } catch (IOException e) {
            // not thrown
            throw new RuntimeException(e);
          }
          String msg = builder.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.warn(msg);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        if (slf4jLogger.isWarnEnabled()) {
          StringBuilder builder = stringBuilder();
          try {
            message.appendTo(Locale.getDefault(), builder);
          } catch (IOException e) {
            // not thrown
            throw new RuntimeException(e);
          }
          String msg = builder.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.warn(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(CharSequence message) {
        if (slf4jLogger.isWarnEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.warn(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(CharSequence message, Throwable cause) {
        if (slf4jLogger.isWarnEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.warn(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier) {
        if (slf4jLogger.isWarnEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            slf4jLogger.warn(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
        if (slf4jLogger.isWarnEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            slf4jLogger.warn(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void logf(String format, Object... args) {
        if (slf4jLogger.isWarnEnabled()) {
          lock.readLock().lock();
          try {
            slf4jLogger.warn(String.format(format, args));
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void batch(Consumer<LogWriter> fn) {
        if (slf4jLogger.isWarnEnabled()) {
          lock.writeLock().lock();
          try {
            fn.accept(this);
          } finally {
            lock.writeLock().unlock();
          }
        }
      }
    };

    this.infoWriter = new LogWriter() {
      @Override
      public void log(LogMessage message) {
        if (slf4jLogger.isInfoEnabled()) {
          StringBuilder builder = stringBuilder();
          try {
            message.appendTo(Locale.getDefault(), builder);
          } catch (IOException e) {
            // not thrown
            throw new RuntimeException(e);
          }
          String msg = builder.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.info(msg);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        if (slf4jLogger.isInfoEnabled()) {
          StringBuilder builder = stringBuilder();
          try {
            message.appendTo(Locale.getDefault(), builder);
          } catch (IOException e) {
            // not thrown
            throw new RuntimeException(e);
          }
          String msg = builder.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.info(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(CharSequence message) {
        if (slf4jLogger.isInfoEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.info(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(CharSequence message, Throwable cause) {
        if (slf4jLogger.isInfoEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.info(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier) {
        if (slf4jLogger.isInfoEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            slf4jLogger.info(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
        if (slf4jLogger.isInfoEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            slf4jLogger.info(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void logf(String format, Object... args) {
        if (slf4jLogger.isInfoEnabled()) {
          lock.readLock().lock();
          try {
            slf4jLogger.info(String.format(format, args));
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void batch(Consumer<LogWriter> fn) {
        if (slf4jLogger.isInfoEnabled()) {
          lock.writeLock().lock();
          try {
            fn.accept(this);
          } finally {
            lock.writeLock().unlock();
          }
        }
      }
    };

    this.debugWriter = new LogWriter() {
      @Override
      public void log(LogMessage message) {
        if (slf4jLogger.isDebugEnabled()) {
          StringBuilder builder = stringBuilder();
          try {
            message.appendTo(Locale.getDefault(), builder);
          } catch (IOException e) {
            // not thrown
            throw new RuntimeException(e);
          }
          String msg = builder.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.debug(msg);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        if (slf4jLogger.isDebugEnabled()) {
          StringBuilder builder = stringBuilder();
          try {
            message.appendTo(Locale.getDefault(), builder);
          } catch (IOException e) {
            // not thrown
            throw new RuntimeException(e);
          }
          String msg = builder.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.debug(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(CharSequence message) {
        if (slf4jLogger.isDebugEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.debug(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(CharSequence message, Throwable cause) {
        if (slf4jLogger.isDebugEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            slf4jLogger.debug(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier) {
        if (slf4jLogger.isDebugEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            slf4jLogger.debug(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
        if (slf4jLogger.isDebugEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            slf4jLogger.debug(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void logf(String format, Object... args) {
        if (slf4jLogger.isDebugEnabled()) {
          lock.readLock().lock();
          try {
            slf4jLogger.debug(String.format(format, args));
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void batch(Consumer<LogWriter> fn) {
        if (slf4jLogger.isDebugEnabled()) {
          lock.writeLock().lock();
          try {
            fn.accept(this);
          } finally {
            lock.writeLock().unlock();
          }
        }
      }
    };
  }

  @Override
  public Level getLevel() {
    if (slf4jLogger.isErrorEnabled()) {
      return Level.ERROR;
    } else if (slf4jLogger.isWarnEnabled()) {
      return Level.WARN;
    } else if (slf4jLogger.isInfoEnabled()) {
      return Level.INFO;
    } else if (slf4jLogger.isDebugEnabled()) {
      return Level.DEBUG;
    }
    return Level.NONE;
  }

  @Override
  public boolean isEnabled(Level level) {
    switch (level) {
      case ERROR:
        return slf4jLogger.isErrorEnabled();
      case WARN:
        return slf4jLogger.isWarnEnabled();
      case INFO:
        return slf4jLogger.isInfoEnabled();
      case DEBUG:
        return slf4jLogger.isDebugEnabled();
      default:
        return false;
    }
  }

  @Override
  public boolean isErrorEnabled() {
    return slf4jLogger.isErrorEnabled();
  }

  @Override
  public LogWriter errorWriter() {
    return errorWriter;
  }

  @Override
  public void error(String pattern, Object... args) {
    if (slf4jLogger.isErrorEnabled()) {
      lock.readLock().lock();
      try {
        slf4jLogger.error(pattern, args);
      } finally {
        lock.readLock().unlock();
      }
    }
  }

  @Override
  public boolean isWarnEnabled() {
    return slf4jLogger.isWarnEnabled();
  }

  @Override
  public LogWriter warnWriter() {
    return warnWriter;
  }

  @Override
  public void warn(String pattern, Object... args) {
    if (slf4jLogger.isWarnEnabled()) {
      lock.readLock().lock();
      try {
        slf4jLogger.warn(pattern, args);
      } finally {
        lock.readLock().unlock();
      }
    }
  }

  @Override
  public boolean isInfoEnabled() {
    return slf4jLogger.isInfoEnabled();
  }

  @Override
  public LogWriter infoWriter() {
    return infoWriter;
  }

  @Override
  public void info(String pattern, Object... args) {
    if (slf4jLogger.isInfoEnabled()) {
      lock.readLock().lock();
      try {
        slf4jLogger.info(pattern, args);
      } finally {
        lock.readLock().unlock();
      }
    }
  }

  @Override
  public boolean isDebugEnabled() {
    return slf4jLogger.isDebugEnabled();
  }

  @Override
  public LogWriter debugWriter() {
    return debugWriter;
  }

  @Override
  public void debug(String pattern, Object... args) {
    if (slf4jLogger.isDebugEnabled()) {
      lock.readLock().lock();
      try {
        slf4jLogger.debug(pattern, args);
      } finally {
        lock.readLock().unlock();
      }
    }
  }

  @Override
  public void batch(Consumer<Logger> fn) {
    lock.writeLock().lock();
    try {
      fn.accept(this);
    } finally {
      lock.writeLock().unlock();
    }
  }

  private StringBuilder stringBuilder() {
    StringBuilder builder = REUSABLE_BUILDER.get().get();
    if (builder == null) {
      builder = new StringBuilder(STRING_BUILDER_CAPACITY);
      REUSABLE_BUILDER.set(new SoftReference<>(builder));
    } else {
      builder.setLength(0);
    }
    return builder;
  }

  private void resetStringBuilder(StringBuilder builder) {
    builder.setLength(STRING_BUILDER_CAPACITY);
    builder.trimToSize();
    builder.setLength(0);
  }
}

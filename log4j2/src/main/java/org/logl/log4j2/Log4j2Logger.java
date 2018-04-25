package org.logl.log4j2;

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
 * An implementation of a {@link Logger} that delegates logging to a {@link org.apache.logging.log4j.Logger
 * org.apache.logging.log4j.Logger}.
 */
public final class Log4j2Logger implements Logger {

  private static final int STRING_BUILDER_CAPACITY = 100;
  private static final ThreadLocal<SoftReference<StringBuilder>> REUSABLE_BUILDER =
      ThreadLocal.withInitial(() -> new SoftReference<>(null));

  private final org.apache.logging.log4j.Logger log4j2Logger;
  // read lock is for all writes to the log, write lock is for batch writes.
  private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
  private final LogWriter errorWriter;
  private final LogWriter warnWriter;
  private final LogWriter infoWriter;
  private final LogWriter debugWriter;

  public Log4j2Logger(org.apache.logging.log4j.Logger log4j2Logger) {
    this.log4j2Logger = log4j2Logger;

    this.errorWriter = new LogWriter() {
      @Override
      public void log(LogMessage message) {
        if (log4j2Logger.isErrorEnabled()) {
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
            log4j2Logger.error(msg);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        if (log4j2Logger.isErrorEnabled()) {
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
            log4j2Logger.error(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(CharSequence message) {
        if (log4j2Logger.isErrorEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            log4j2Logger.error(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(CharSequence message, Throwable cause) {
        if (log4j2Logger.isErrorEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            log4j2Logger.error(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier) {
        if (log4j2Logger.isErrorEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            log4j2Logger.error(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
        if (log4j2Logger.isErrorEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            log4j2Logger.error(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void logf(String format, Object... args) {
        if (log4j2Logger.isErrorEnabled()) {
          lock.readLock().lock();
          try {
            log4j2Logger.error(String.format(format, args));
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void batch(Consumer<LogWriter> fn) {
        if (log4j2Logger.isErrorEnabled()) {
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
        if (log4j2Logger.isWarnEnabled()) {
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
            log4j2Logger.warn(msg);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        if (log4j2Logger.isWarnEnabled()) {
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
            log4j2Logger.warn(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(CharSequence message) {
        if (log4j2Logger.isWarnEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            log4j2Logger.warn(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(CharSequence message, Throwable cause) {
        if (log4j2Logger.isWarnEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            log4j2Logger.warn(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier) {
        if (log4j2Logger.isWarnEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            log4j2Logger.warn(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
        if (log4j2Logger.isWarnEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            log4j2Logger.warn(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void logf(String format, Object... args) {
        if (log4j2Logger.isWarnEnabled()) {
          lock.readLock().lock();
          try {
            log4j2Logger.warn(String.format(format, args));
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void batch(Consumer<LogWriter> fn) {
        if (log4j2Logger.isWarnEnabled()) {
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
        if (log4j2Logger.isInfoEnabled()) {
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
            log4j2Logger.info(msg);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        if (log4j2Logger.isInfoEnabled()) {
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
            log4j2Logger.info(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(CharSequence message) {
        if (log4j2Logger.isInfoEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            log4j2Logger.info(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(CharSequence message, Throwable cause) {
        if (log4j2Logger.isInfoEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            log4j2Logger.info(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier) {
        if (log4j2Logger.isInfoEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            log4j2Logger.info(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
        if (log4j2Logger.isInfoEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            log4j2Logger.info(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void logf(String format, Object... args) {
        if (log4j2Logger.isInfoEnabled()) {
          lock.readLock().lock();
          try {
            log4j2Logger.info(String.format(format, args));
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void batch(Consumer<LogWriter> fn) {
        if (log4j2Logger.isInfoEnabled()) {
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
        if (log4j2Logger.isDebugEnabled()) {
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
            log4j2Logger.debug(msg);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        if (log4j2Logger.isDebugEnabled()) {
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
            log4j2Logger.debug(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
          resetStringBuilder(builder);
        }
      }

      @Override
      public void log(CharSequence message) {
        if (log4j2Logger.isDebugEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            log4j2Logger.debug(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(CharSequence message, Throwable cause) {
        if (log4j2Logger.isDebugEnabled()) {
          String msg = message.toString();
          lock.readLock().lock();
          try {
            log4j2Logger.debug(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier) {
        if (log4j2Logger.isDebugEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            log4j2Logger.debug(msg);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
        if (log4j2Logger.isDebugEnabled()) {
          String msg = messageSupplier.get().toString();
          lock.readLock().lock();
          try {
            log4j2Logger.debug(msg, cause);
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void logf(String format, Object... args) {
        if (log4j2Logger.isDebugEnabled()) {
          lock.readLock().lock();
          try {
            log4j2Logger.debug(String.format(format, args));
          } finally {
            lock.readLock().unlock();
          }
        }
      }

      @Override
      public void batch(Consumer<LogWriter> fn) {
        if (log4j2Logger.isDebugEnabled()) {
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
    if (log4j2Logger.isErrorEnabled()) {
      return Level.ERROR;
    } else if (log4j2Logger.isWarnEnabled()) {
      return Level.WARN;
    } else if (log4j2Logger.isInfoEnabled()) {
      return Level.INFO;
    } else if (log4j2Logger.isDebugEnabled()) {
      return Level.DEBUG;
    }
    return Level.NONE;
  }

  @Override
  public boolean isEnabled(Level level) {
    switch (level) {
      case ERROR:
        return log4j2Logger.isErrorEnabled();
      case WARN:
        return log4j2Logger.isWarnEnabled();
      case INFO:
        return log4j2Logger.isInfoEnabled();
      case DEBUG:
        return log4j2Logger.isDebugEnabled();
      default:
        return false;
    }
  }

  @Override
  public boolean isErrorEnabled() {
    return log4j2Logger.isErrorEnabled();
  }

  @Override
  public LogWriter errorWriter() {
    return errorWriter;
  }

  @Override
  public void error(String pattern, Object... args) {
    if (log4j2Logger.isErrorEnabled()) {
      lock.readLock().lock();
      try {
        log4j2Logger.error(pattern, args);
      } finally {
        lock.readLock().unlock();
      }
    }
  }

  @Override
  public boolean isWarnEnabled() {
    return log4j2Logger.isWarnEnabled();
  }

  @Override
  public LogWriter warnWriter() {
    return warnWriter;
  }

  @Override
  public void warn(String pattern, Object... args) {
    if (log4j2Logger.isWarnEnabled()) {
      lock.readLock().lock();
      try {
        log4j2Logger.warn(pattern, args);
      } finally {
        lock.readLock().unlock();
      }
    }
  }

  @Override
  public boolean isInfoEnabled() {
    return log4j2Logger.isInfoEnabled();
  }

  @Override
  public LogWriter infoWriter() {
    return infoWriter;
  }

  @Override
  public void info(String pattern, Object... args) {
    if (log4j2Logger.isInfoEnabled()) {
      lock.readLock().lock();
      try {
        log4j2Logger.info(pattern, args);
      } finally {
        lock.readLock().unlock();
      }
    }
  }

  @Override
  public boolean isDebugEnabled() {
    return log4j2Logger.isDebugEnabled();
  }

  @Override
  public LogWriter debugWriter() {
    return debugWriter;
  }

  @Override
  public void debug(String pattern, Object... args) {
    if (log4j2Logger.isDebugEnabled()) {
      lock.readLock().lock();
      try {
        log4j2Logger.debug(pattern, args);
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

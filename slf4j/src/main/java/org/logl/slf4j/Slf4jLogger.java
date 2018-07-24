package org.logl.slf4j;

import static java.util.Objects.requireNonNull;

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
   * @param logger The log4j2 logger to output to.
   */
  public Slf4jLogger(org.slf4j.Logger logger) {
    requireNonNull(logger);
    this.slf4jLogger = logger;

    this.errorWriter = new LogWriter() {
      @Override
      public void log(LogMessage message) {
        error(message);
      }

      @Override
      public void log(CharSequence message) {
        error(message);
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier) {
        error(messageSupplier);
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        error(message, cause);
      }

      @Override
      public void log(CharSequence message, Throwable cause) {
        error(message, cause);
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
        error(messageSupplier, cause);
      }

      @Override
      public void log(String pattern, Object... args) {
        error(pattern, args);
      }

      @Override
      public void logf(String format, Object... args) {
        errorf(format, args);
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
        warn(message);
      }

      @Override
      public void log(CharSequence message) {
        warn(message);
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier) {
        warn(messageSupplier);
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        warn(message, cause);
      }

      @Override
      public void log(CharSequence message, Throwable cause) {
        warn(message, cause);
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
        warn(messageSupplier, cause);
      }

      @Override
      public void log(String pattern, Object... args) {
        warn(pattern, args);
      }

      @Override
      public void logf(String format, Object... args) {
        warnf(format, args);
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
        info(message);
      }

      @Override
      public void log(CharSequence message) {
        info(message);
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier) {
        info(messageSupplier);
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        info(message, cause);
      }

      @Override
      public void log(CharSequence message, Throwable cause) {
        info(message, cause);
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
        info(messageSupplier, cause);
      }

      @Override
      public void log(String pattern, Object... args) {
        info(pattern, args);
      }

      @Override
      public void logf(String format, Object... args) {
        infof(format, args);
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
        debug(message);
      }

      @Override
      public void log(CharSequence message) {
        debug(message);
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier) {
        debug(messageSupplier);
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        debug(message, cause);
      }

      @Override
      public void log(CharSequence message, Throwable cause) {
        debug(message, cause);
      }

      @Override
      public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
        debug(messageSupplier, cause);
      }

      @Override
      public void log(String pattern, Object... args) {
        debug(pattern, args);
      }

      @Override
      public void logf(String format, Object... args) {
        debugf(format, args);
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
    if (isDebugEnabled()) {
      return Level.DEBUG;
    } else if (isInfoEnabled()) {
      return Level.INFO;
    } else if (isWarnEnabled()) {
      return Level.WARN;
    }  else if (isErrorEnabled()) {
      return Level.ERROR;
    }
    return Level.NONE;
  }

  @Override
  public boolean isEnabled(Level level) {
    switch (level) {
      case ERROR:
        return isErrorEnabled();
      case WARN:
        return isWarnEnabled();
      case INFO:
        return isInfoEnabled();
      case DEBUG:
        return isDebugEnabled();
      default:
        return false;
    }
  }

  @Override
  public boolean isErrorEnabled() {
    return slf4jLogger.isErrorEnabled();
  }

  @Override
  public void error(LogMessage message) {
    if (isErrorEnabled()) {
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
  public void error(CharSequence message) {
    if (isErrorEnabled()) {
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
  public void error(Supplier<? extends CharSequence> messageSupplier) {
    if (isErrorEnabled()) {
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
  public void error(LogMessage message, Throwable cause) {
    if (isErrorEnabled()) {
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
  public void error(CharSequence message, Throwable cause) {
    if (isErrorEnabled()) {
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
  public void error(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    if (isErrorEnabled()) {
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
  public void error(String pattern, Object... args) {
    if (isErrorEnabled()) {
      unguardedError(pattern, args);
    }
  }

  private void unguardedError(String pattern, Object[] args) {
    lock.readLock().lock();
    try {
      slf4jLogger.error(pattern, args);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void error(String pattern, Object arg) {
    if (isErrorEnabled()) {
      unguardedError(pattern, new Object[] {arg});
    }
  }

  @Override
  public void error(String pattern, Object arg1, Object arg2) {
    if (isErrorEnabled()) {
      unguardedError(pattern, new Object[] {arg1, arg2});
    }
  }

  @Override
  public void error(String pattern, Object arg1, Object arg2, Object arg3) {
    if (isErrorEnabled()) {
      unguardedError(pattern, new Object[] {arg1, arg2, arg3});
    }
  }

  @Override
  public void error(String pattern, Object arg1, Object arg2, Object arg3, Object arg4) {
    if (isErrorEnabled()) {
      unguardedError(pattern, new Object[] {arg1, arg2, arg3, arg4});
    }
  }

  @Override
  public void error(String pattern, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
    if (isErrorEnabled()) {
      unguardedError(pattern, new Object[] {arg1, arg2, arg3, arg4, arg5});
    }
  }

  @Override
  public void errorf(String format, Object... args) {
    if (isErrorEnabled()) {
      unguardedErrorf(format, args);
    }
  }

  private void unguardedErrorf(String format, Object[] args) {
    lock.readLock().lock();
    try {
      slf4jLogger.error(String.format(format, args));
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void errorf(String format, Object arg) {
    if (isErrorEnabled()) {
      unguardedErrorf(format, new Object[] {arg});
    }
  }

  @Override
  public void errorf(String format, Object arg1, Object arg2) {
    if (isErrorEnabled()) {
      unguardedErrorf(format, new Object[] {arg1, arg2});
    }
  }

  @Override
  public void errorf(String format, Object arg1, Object arg2, Object arg3) {
    if (isErrorEnabled()) {
      unguardedErrorf(format, new Object[] {arg1, arg2, arg3});
    }
  }

  @Override
  public void errorf(String format, Object arg1, Object arg2, Object arg3, Object arg4) {
    if (isErrorEnabled()) {
      unguardedErrorf(format, new Object[] {arg1, arg2, arg3, arg4});
    }
  }

  @Override
  public void errorf(String format, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
    if (isErrorEnabled()) {
      unguardedErrorf(format, new Object[] {arg1, arg2, arg3, arg4, arg5});
    }
  }

  @Override
  public LogWriter errorWriter() {
    return errorWriter;
  }

  @Override
  public boolean isWarnEnabled() {
    return slf4jLogger.isWarnEnabled();
  }

  @Override
  public void warn(LogMessage message) {
    if (isWarnEnabled()) {
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
  public void warn(CharSequence message) {
    if (isWarnEnabled()) {
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
  public void warn(Supplier<? extends CharSequence> messageSupplier) {
    if (isWarnEnabled()) {
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
  public void warn(LogMessage message, Throwable cause) {
    if (isWarnEnabled()) {
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
  public void warn(CharSequence message, Throwable cause) {
    if (isWarnEnabled()) {
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
  public void warn(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    if (isWarnEnabled()) {
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
  public void warn(String pattern, Object... args) {
    if (isWarnEnabled()) {
      unguardedWarn(pattern, args);
    }
  }

  private void unguardedWarn(String pattern, Object[] args) {
    lock.readLock().lock();
    try {
      slf4jLogger.warn(pattern, args);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void warn(String pattern, Object arg) {
    if (isWarnEnabled()) {
      unguardedWarn(pattern, new Object[] {arg});
    }
  }

  @Override
  public void warn(String pattern, Object arg1, Object arg2) {
    if (isWarnEnabled()) {
      unguardedWarn(pattern, new Object[] {arg1, arg2});
    }
  }

  @Override
  public void warn(String pattern, Object arg1, Object arg2, Object arg3) {
    if (isWarnEnabled()) {
      unguardedWarn(pattern, new Object[] {arg1, arg2, arg3});
    }
  }

  @Override
  public void warn(String pattern, Object arg1, Object arg2, Object arg3, Object arg4) {
    if (isWarnEnabled()) {
      unguardedWarn(pattern, new Object[] {arg1, arg2, arg3, arg4});
    }
  }

  @Override
  public void warn(String pattern, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
    if (isWarnEnabled()) {
      unguardedWarn(pattern, new Object[] {arg1, arg2, arg3, arg4, arg5});
    }
  }

  @Override
  public void warnf(String format, Object... args) {
    if (isWarnEnabled()) {
      unguardedWarnf(format, args);
    }
  }

  private void unguardedWarnf(String format, Object[] args) {
    lock.readLock().lock();
    try {
      slf4jLogger.warn(String.format(format, args));
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void warnf(String format, Object arg) {
    if (isWarnEnabled()) {
      unguardedWarnf(format, new Object[] {arg});
    }
  }

  @Override
  public void warnf(String format, Object arg1, Object arg2) {
    if (isWarnEnabled()) {
      unguardedWarnf(format, new Object[] {arg1, arg2});
    }
  }

  @Override
  public void warnf(String format, Object arg1, Object arg2, Object arg3) {
    if (isWarnEnabled()) {
      unguardedWarnf(format, new Object[] {arg1, arg2, arg3});
    }
  }

  @Override
  public void warnf(String format, Object arg1, Object arg2, Object arg3, Object arg4) {
    if (isWarnEnabled()) {
      unguardedWarnf(format, new Object[] {arg1, arg2, arg3, arg4});
    }
  }

  @Override
  public void warnf(String format, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
    if (isWarnEnabled()) {
      unguardedWarnf(format, new Object[] {arg1, arg2, arg3, arg4, arg5});
    }
  }

  @Override
  public LogWriter warnWriter() {
    return warnWriter;
  }

  @Override
  public boolean isInfoEnabled() {
    return slf4jLogger.isInfoEnabled();
  }

  @Override
  public void info(LogMessage message) {
    if (isInfoEnabled()) {
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
  public void info(CharSequence message) {
    if (isInfoEnabled()) {
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
  public void info(Supplier<? extends CharSequence> messageSupplier) {
    if (isInfoEnabled()) {
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
  public void info(LogMessage message, Throwable cause) {
    if (isInfoEnabled()) {
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
  public void info(CharSequence message, Throwable cause) {
    if (isInfoEnabled()) {
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
  public void info(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    if (isInfoEnabled()) {
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
  public void info(String pattern, Object... args) {
    if (isInfoEnabled()) {
      unguardedInfo(pattern, args);
    }
  }

  private void unguardedInfo(String pattern, Object[] args) {
    lock.readLock().lock();
    try {
      slf4jLogger.info(pattern, args);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void info(String pattern, Object arg) {
    if (isInfoEnabled()) {
      unguardedInfo(pattern, new Object[] {arg});
    }
  }

  @Override
  public void info(String pattern, Object arg1, Object arg2) {
    if (isInfoEnabled()) {
      unguardedInfo(pattern, new Object[] {arg1, arg2});
    }
  }

  @Override
  public void info(String pattern, Object arg1, Object arg2, Object arg3) {
    if (isInfoEnabled()) {
      unguardedInfo(pattern, new Object[] {arg1, arg2, arg3});
    }
  }

  @Override
  public void info(String pattern, Object arg1, Object arg2, Object arg3, Object arg4) {
    if (isInfoEnabled()) {
      unguardedInfo(pattern, new Object[] {arg1, arg2, arg3, arg4});
    }
  }

  @Override
  public void info(String pattern, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
    if (isInfoEnabled()) {
      unguardedInfo(pattern, new Object[] {arg1, arg2, arg3, arg4, arg5});
    }
  }

  @Override
  public void infof(String format, Object... args) {
    if (isInfoEnabled()) {
      unguardedInfof(format, args);
    }
  }

  private void unguardedInfof(String format, Object[] args) {
    lock.readLock().lock();
    try {
      slf4jLogger.info(String.format(format, args));
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void infof(String format, Object arg) {
    if (isInfoEnabled()) {
      unguardedInfof(format, new Object[] {arg});
    }
  }

  @Override
  public void infof(String format, Object arg1, Object arg2) {
    if (isInfoEnabled()) {
      unguardedInfof(format, new Object[] {arg1, arg2});
    }
  }

  @Override
  public void infof(String format, Object arg1, Object arg2, Object arg3) {
    if (isInfoEnabled()) {
      unguardedInfof(format, new Object[] {arg1, arg2, arg3});
    }
  }

  @Override
  public void infof(String format, Object arg1, Object arg2, Object arg3, Object arg4) {
    if (isInfoEnabled()) {
      unguardedInfof(format, new Object[] {arg1, arg2, arg3, arg4});
    }
  }

  @Override
  public void infof(String format, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
    if (isInfoEnabled()) {
      unguardedInfof(format, new Object[] {arg1, arg2, arg3, arg4, arg5});
    }
  }

  @Override
  public LogWriter infoWriter() {
    return infoWriter;
  }

  @Override
  public boolean isDebugEnabled() {
    return slf4jLogger.isDebugEnabled();
  }

  @Override
  public void debug(LogMessage message) {
    if (isDebugEnabled()) {
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
  public void debug(CharSequence message) {
    if (isDebugEnabled()) {
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
  public void debug(Supplier<? extends CharSequence> messageSupplier) {
    if (isDebugEnabled()) {
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
  public void debug(LogMessage message, Throwable cause) {
    if (isDebugEnabled()) {
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
  public void debug(CharSequence message, Throwable cause) {
    if (isDebugEnabled()) {
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
  public void debug(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    if (isDebugEnabled()) {
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
  public void debug(String pattern, Object... args) {
    if (isDebugEnabled()) {
      unguardedDebug(pattern, args);
    }
  }

  private void unguardedDebug(String pattern, Object[] args) {
    lock.readLock().lock();
    try {
      slf4jLogger.debug(pattern, args);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void debug(String pattern, Object arg) {
    if (isDebugEnabled()) {
      unguardedDebug(pattern, new Object[] {arg});
    }
  }

  @Override
  public void debug(String pattern, Object arg1, Object arg2) {
    if (isDebugEnabled()) {
      unguardedDebug(pattern, new Object[] {arg1, arg2});
    }
  }

  @Override
  public void debug(String pattern, Object arg1, Object arg2, Object arg3) {
    if (isDebugEnabled()) {
      unguardedDebug(pattern, new Object[] {arg1, arg2, arg3});
    }
  }

  @Override
  public void debug(String pattern, Object arg1, Object arg2, Object arg3, Object arg4) {
    if (isDebugEnabled()) {
      unguardedDebug(pattern, new Object[] {arg1, arg2, arg3, arg4});
    }
  }

  @Override
  public void debug(String pattern, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
    if (isDebugEnabled()) {
      unguardedDebug(pattern, new Object[] {arg1, arg2, arg3, arg4, arg5});
    }
  }

  @Override
  public void debugf(String format, Object... args) {
    if (isDebugEnabled()) {
      unguardedDebugf(format, args);
    }
  }

  private void unguardedDebugf(String format, Object[] args) {
    lock.readLock().lock();
    try {
      slf4jLogger.debug(String.format(format, args));
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void debugf(String format, Object arg) {
    if (isDebugEnabled()) {
      unguardedDebugf(format, new Object[] {arg});
    }
  }

  @Override
  public void debugf(String format, Object arg1, Object arg2) {
    if (isDebugEnabled()) {
      unguardedDebugf(format, new Object[] {arg1, arg2});
    }
  }

  @Override
  public void debugf(String format, Object arg1, Object arg2, Object arg3) {
    if (isDebugEnabled()) {
      unguardedDebugf(format, new Object[] {arg1, arg2, arg3});
    }
  }

  @Override
  public void debugf(String format, Object arg1, Object arg2, Object arg3, Object arg4) {
    if (isDebugEnabled()) {
      unguardedDebugf(format, new Object[] {arg1, arg2, arg3, arg4});
    }
  }

  @Override
  public void debugf(String format, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
    if (isDebugEnabled()) {
      unguardedDebugf(format, new Object[] {arg1, arg2, arg3, arg4, arg5});
    }
  }

  @Override
  public LogWriter debugWriter() {
    return debugWriter;
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

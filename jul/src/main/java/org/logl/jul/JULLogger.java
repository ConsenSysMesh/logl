/*
 * Copyright 2018 ConsenSys AG.
 *
 * This code is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files(the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and / or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.logl.jul;

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
 * An implementation of a {@link Logger} that delegates logging to a {@link java.util.logging.Logger
 * java.util.logging.Logger}.
 */
public final class JULLogger implements Logger {

  private static final int STRING_BUILDER_CAPACITY = 100;
  private static final ThreadLocal<SoftReference<StringBuilder>> REUSABLE_BUILDER =
      ThreadLocal.withInitial(() -> new SoftReference<>(null));

  private final java.util.logging.Logger julLogger;
  // read lock is for all writes to the log, write lock is for batch writes.
  private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
  private final LogWriter errorWriter;
  private final LogWriter warnWriter;
  private final LogWriter infoWriter;
  private final LogWriter debugWriter;

  /**
   * @param logger The {@link java.util.logging.Logger java.util.logging.Logger} to output to.
   */
  public JULLogger(java.util.logging.Logger logger) {
    requireNonNull(logger);
    this.julLogger = logger;
    this.errorWriter = new JULLogWriter(java.util.logging.Level.SEVERE);
    this.warnWriter = new JULLogWriter(java.util.logging.Level.WARNING);
    this.infoWriter = new JULLogWriter(java.util.logging.Level.INFO);
    this.debugWriter = new JULLogWriter(java.util.logging.Level.FINE);
  }

  @Override
  public Level getLevel() {
    if (julLogger.isLoggable(java.util.logging.Level.SEVERE)) {
      return Level.ERROR;
    } else if (julLogger.isLoggable(java.util.logging.Level.WARNING)) {
      return Level.WARN;
    } else if (julLogger.isLoggable(java.util.logging.Level.INFO)) {
      return Level.INFO;
    } else if (julLogger.isLoggable(java.util.logging.Level.FINE)) {
      return Level.DEBUG;
    }
    return Level.NONE;
  }

  @Override
  public boolean isEnabled(Level level) {
    switch (level) {
      case ERROR:
        return julLogger.isLoggable(java.util.logging.Level.SEVERE);
      case WARN:
        return julLogger.isLoggable(java.util.logging.Level.WARNING);
      case INFO:
        return julLogger.isLoggable(java.util.logging.Level.INFO);
      case DEBUG:
        return julLogger.isLoggable(java.util.logging.Level.FINE);
      default:
        return false;
    }
  }

  @Override
  public boolean isErrorEnabled() {
    return julLogger.isLoggable(java.util.logging.Level.SEVERE);
  }

  @Override
  public LogWriter errorWriter() {
    return errorWriter;
  }

  @Override
  public boolean isWarnEnabled() {
    return julLogger.isLoggable(java.util.logging.Level.WARNING);
  }

  @Override
  public LogWriter warnWriter() {
    return warnWriter;
  }

  @Override
  public boolean isInfoEnabled() {
    return julLogger.isLoggable(java.util.logging.Level.INFO);
  }

  @Override
  public LogWriter infoWriter() {
    return infoWriter;
  }

  @Override
  public boolean isDebugEnabled() {
    return julLogger.isLoggable(java.util.logging.Level.FINE);
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

  private final class JULLogWriter implements LogWriter {
    private java.util.logging.Level level;

    JULLogWriter(java.util.logging.Level level) {
      this.level = level;
    }

    @Override
    public void log(LogMessage message) {
      if (julLogger.isLoggable(level)) {
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
          julLogger.log(level, msg);
        } finally {
          lock.readLock().unlock();
        }
        resetStringBuilder(builder);
      }
    }

    @Override
    public void log(LogMessage message, Throwable cause) {
      if (julLogger.isLoggable(level)) {
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
          julLogger.log(level, msg, cause);
        } finally {
          lock.readLock().unlock();
        }
        resetStringBuilder(builder);
      }
    }

    @Override
    public void log(CharSequence message) {
      if (julLogger.isLoggable(level)) {
        String msg = message.toString();
        lock.readLock().lock();
        try {
          julLogger.log(level, msg);
        } finally {
          lock.readLock().unlock();
        }
      }
    }

    @Override
    public void log(CharSequence message, Throwable cause) {
      if (julLogger.isLoggable(level)) {
        String msg = message.toString();
        lock.readLock().lock();
        try {
          julLogger.log(level, msg, cause);
        } finally {
          lock.readLock().unlock();
        }
      }
    }

    @Override
    public void log(Supplier<? extends CharSequence> messageSupplier) {
      if (julLogger.isLoggable(level)) {
        String msg = messageSupplier.get().toString();
        lock.readLock().lock();
        try {
          julLogger.log(level, msg);
        } finally {
          lock.readLock().unlock();
        }
      }
    }

    @Override
    public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
      if (julLogger.isLoggable(level)) {
        String msg = messageSupplier.get().toString();
        lock.readLock().lock();
        try {
          julLogger.log(level, msg, cause);
        } finally {
          lock.readLock().unlock();
        }
      }
    }

    @Override
    public void logf(String format, Object... args) {
      if (julLogger.isLoggable(level)) {
        lock.readLock().lock();
        try {
          julLogger.log(level, String.format(format, args));
        } finally {
          lock.readLock().unlock();
        }
      }
    }

    @Override
    public void batch(Consumer<LogWriter> fn) {
      if (julLogger.isLoggable(level)) {
        lock.writeLock().lock();
        try {
          fn.accept(this);
        } finally {
          lock.writeLock().unlock();
        }
      }
    }
  }
}

package org.logl.logl;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.logl.Level;
import org.logl.LogMessage;
import org.logl.LogWriter;
import org.logl.Logger;
import org.logl.LoggerProvider;

/**
 * A {@link LoggerProvider} that duplicates logging interactions.
 */
public final class DuplicatingLogger implements AdjustableLogger {

  private List<Logger> loggers;
  private final AtomicReference<Level> level = new AtomicReference<>(null);
  private DuplicatingLogWriter errorWriter;
  private DuplicatingLogWriter warnWriter;
  private DuplicatingLogWriter infoWriter;
  private DuplicatingLogWriter debugWriter;

  /**
   * @param loggers The {@link Logger}s that should receiving interactions.
   */
  public DuplicatingLogger(Logger... loggers) {
    this(Arrays.asList(loggers));
  }

  /**
   * @param loggers The {@link Logger}s that should receiving interactions.
   */
  public DuplicatingLogger(Collection<Logger> loggers) {
    this.loggers = new CopyOnWriteArrayList<>(loggers);
    this.level.set(this.loggers.stream().map(Logger::getLevel).min(Level::compareTo).orElse(Level.NONE));

    List<LogWriter> errorWriters = new ArrayList<>(this.loggers.size());
    List<LogWriter> warnWriters = new ArrayList<>(this.loggers.size());
    List<LogWriter> infoWriters = new ArrayList<>(this.loggers.size());
    List<LogWriter> debugWriters = new ArrayList<>(this.loggers.size());

    for (Logger logger : this.loggers) {
      errorWriters.add(logger.errorWriter());
      warnWriters.add(logger.warnWriter());
      infoWriters.add(logger.infoWriter());
      debugWriters.add(logger.debugWriter());
    }

    this.errorWriter = new DuplicatingLogWriter(Level.ERROR, this, errorWriters);
    this.warnWriter = new DuplicatingLogWriter(Level.WARN, this, warnWriters);
    this.infoWriter = new DuplicatingLogWriter(Level.INFO, this, infoWriters);
    this.debugWriter = new DuplicatingLogWriter(Level.DEBUG, this, debugWriters);
  }

  /**
   * Remove a {@link Logger} from the duplicate set.
   *
   * @param logger The logger to remove.
   * @return {@code true} if the logger was removed.
   */
  public boolean removeLogger(Logger logger) {
    if (!loggers.remove(logger)) {
      return false;
    }
    this.errorWriter.remove(logger.errorWriter());
    this.warnWriter.remove(logger.warnWriter());
    this.infoWriter.remove(logger.infoWriter());
    this.debugWriter.remove(logger.debugWriter());
    return true;
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

  // package-private variant of isEnabled that does no input checking
  boolean checkLevel(Level level) {
    return level.compareTo(this.level.get()) <= 0;
  }

  @Override
  public boolean isErrorEnabled() {
    return checkLevel(Level.ERROR);
  }

  @Override
  public void error(LogMessage message) {
    if (checkLevel(Level.ERROR)) {
      for (Logger logger : loggers) {
        logger.error(message);
      }
    }
  }

  @Override
  public void error(CharSequence message) {
    if (checkLevel(Level.ERROR)) {
      for (Logger logger : loggers) {
        logger.error(message);
      }
    }
  }

  @Override
  public void error(Supplier<? extends CharSequence> messageSupplier) {
    if (checkLevel(Level.ERROR)) {
      CharSequence message = null;
      for (Logger logger : loggers) {
        if (logger.isErrorEnabled()) {
          if (message == null) {
            message = messageSupplier.get();
          }
          logger.error(message);
        }
      }
    }
  }

  @Override
  public void error(LogMessage message, Throwable cause) {
    if (checkLevel(Level.ERROR)) {
      for (Logger logger : loggers) {
        logger.error(message, cause);
      }
    }
  }

  @Override
  public void error(CharSequence message, Throwable cause) {
    if (checkLevel(Level.ERROR)) {
      for (Logger logger : loggers) {
        logger.error(message, cause);
      }
    }
  }

  @Override
  public void error(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    if (checkLevel(Level.ERROR)) {
      CharSequence message = null;
      for (Logger logger : loggers) {
        if (logger.isErrorEnabled()) {
          if (message == null) {
            message = messageSupplier.get();
          }
          logger.error(message, cause);
        }
      }
    }
  }

  @Override
  public void error(String pattern, Object... args) {
    if (checkLevel(Level.ERROR)) {
      for (Logger logger : loggers) {
        logger.error(pattern, args);
      }
    }
  }

  @Override
  public void errorf(String format, Object... args) {
    if (checkLevel(Level.ERROR)) {
      for (Logger logger : loggers) {
        logger.error(format, args);
      }
    }
  }

  @Override
  public LogWriter errorWriter() {
    return this.errorWriter;
  }

  @Override
  public boolean isWarnEnabled() {
    return Level.WARN.compareTo(this.level.get()) <= 0;
  }

  @Override
  public void warn(LogMessage message) {
    if (checkLevel(Level.WARN)) {
      for (Logger logger : loggers) {
        logger.warn(message);
      }
    }
  }

  @Override
  public void warn(CharSequence message) {
    if (checkLevel(Level.WARN)) {
      for (Logger logger : loggers) {
        logger.warn(message);
      }
    }
  }

  @Override
  public void warn(Supplier<? extends CharSequence> messageSupplier) {
    if (checkLevel(Level.WARN)) {
      CharSequence message = null;
      for (Logger logger : loggers) {
        if (logger.isWarnEnabled()) {
          if (message == null) {
            message = messageSupplier.get();
          }
          logger.warn(message);
        }
      }
    }
  }

  @Override
  public void warn(LogMessage message, Throwable cause) {
    if (checkLevel(Level.WARN)) {
      for (Logger logger : loggers) {
        logger.warn(message, cause);
      }
    }
  }

  @Override
  public void warn(CharSequence message, Throwable cause) {
    if (checkLevel(Level.WARN)) {
      for (Logger logger : loggers) {
        logger.warn(message, cause);
      }
    }
  }

  @Override
  public void warn(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    if (checkLevel(Level.WARN)) {
      CharSequence message = null;
      for (Logger logger : loggers) {
        if (logger.isWarnEnabled()) {
          if (message == null) {
            message = messageSupplier.get();
          }
          logger.warn(message, cause);
        }
      }
    }
  }

  @Override
  public void warn(String pattern, Object... args) {
    if (checkLevel(Level.WARN)) {
      for (Logger logger : loggers) {
        logger.warn(pattern, args);
      }
    }
  }

  @Override
  public void warnf(String format, Object... args) {
    if (checkLevel(Level.WARN)) {
      for (Logger logger : loggers) {
        logger.warn(format, args);
      }
    }
  }

  @Override
  public LogWriter warnWriter() {
    return this.warnWriter;
  }

  @Override
  public boolean isInfoEnabled() {
    return Level.INFO.compareTo(this.level.get()) <= 0;
  }

  @Override
  public void info(LogMessage message) {
    if (checkLevel(Level.INFO)) {
      for (Logger logger : loggers) {
        logger.info(message);
      }
    }
  }

  @Override
  public void info(CharSequence message) {
    if (checkLevel(Level.INFO)) {
      for (Logger logger : loggers) {
        logger.info(message);
      }
    }
  }

  @Override
  public void info(Supplier<? extends CharSequence> messageSupplier) {
    if (checkLevel(Level.INFO)) {
      CharSequence message = null;
      for (Logger logger : loggers) {
        if (logger.isInfoEnabled()) {
          if (message == null) {
            message = messageSupplier.get();
          }
          logger.info(message);
        }
      }
    }
  }

  @Override
  public void info(LogMessage message, Throwable cause) {
    if (checkLevel(Level.INFO)) {
      for (Logger logger : loggers) {
        logger.info(message, cause);
      }
    }
  }

  @Override
  public void info(CharSequence message, Throwable cause) {
    if (checkLevel(Level.INFO)) {
      for (Logger logger : loggers) {
        logger.info(message, cause);
      }
    }
  }

  @Override
  public void info(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    if (checkLevel(Level.INFO)) {
      CharSequence message = null;
      for (Logger logger : loggers) {
        if (logger.isInfoEnabled()) {
          if (message == null) {
            message = messageSupplier.get();
          }
          logger.info(message, cause);
        }
      }
    }
  }

  @Override
  public void info(String pattern, Object... args) {
    if (checkLevel(Level.INFO)) {
      for (Logger logger : loggers) {
        logger.info(pattern, args);
      }
    }
  }

  @Override
  public void infof(String format, Object... args) {
    if (checkLevel(Level.INFO)) {
      for (Logger logger : loggers) {
        logger.info(format, args);
      }
    }
  }

  @Override
  public LogWriter infoWriter() {
    return this.infoWriter;
  }

  @Override
  public boolean isDebugEnabled() {
    return Level.DEBUG.compareTo(this.level.get()) <= 0;
  }

  @Override
  public void debug(LogMessage message) {
    if (checkLevel(Level.DEBUG)) {
      for (Logger logger : loggers) {
        logger.debug(message);
      }
    }
  }

  @Override
  public void debug(CharSequence message) {
    if (checkLevel(Level.DEBUG)) {
      for (Logger logger : loggers) {
        logger.debug(message);
      }
    }
  }

  @Override
  public void debug(Supplier<? extends CharSequence> messageSupplier) {
    if (checkLevel(Level.DEBUG)) {
      CharSequence message = null;
      for (Logger logger : loggers) {
        if (logger.isDebugEnabled()) {
          if (message == null) {
            message = messageSupplier.get();
          }
          logger.debug(message);
        }
      }
    }
  }

  @Override
  public void debug(LogMessage message, Throwable cause) {
    if (checkLevel(Level.DEBUG)) {
      for (Logger logger : loggers) {
        logger.debug(message, cause);
      }
    }
  }

  @Override
  public void debug(CharSequence message, Throwable cause) {
    if (checkLevel(Level.DEBUG)) {
      for (Logger logger : loggers) {
        logger.debug(message, cause);
      }
    }
  }

  @Override
  public void debug(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    if (checkLevel(Level.DEBUG)) {
      CharSequence message = null;
      for (Logger logger : loggers) {
        if (logger.isDebugEnabled()) {
          if (message == null) {
            message = messageSupplier.get();
          }
          logger.debug(message, cause);
        }
      }
    }
  }

  @Override
  public void debug(String pattern, Object... args) {
    if (checkLevel(Level.DEBUG)) {
      for (Logger logger : loggers) {
        logger.debug(pattern, args);
      }
    }
  }

  @Override
  public void debugf(String format, Object... args) {
    if (checkLevel(Level.DEBUG)) {
      for (Logger logger : loggers) {
        logger.debug(format, args);
      }
    }
  }

  @Override
  public LogWriter debugWriter() {
    return this.debugWriter;
  }

  @Override
  public void batch(Consumer<Logger> fn) {
    batch(0, new ArrayList<>(loggers.size()), fn);
  }

  private void batch(int index, ArrayList<Logger> batchLoggers, Consumer<Logger> fn) {
    if (index < loggers.size()) {
      Logger logger = loggers.get(index);
      logger.batch(bulkLogger -> {
        batchLoggers.add(bulkLogger);
        batch(index + 1, batchLoggers, fn);
      });
    } else {
      fn.accept(new DuplicatingLogger(batchLoggers));
    }
  }
}

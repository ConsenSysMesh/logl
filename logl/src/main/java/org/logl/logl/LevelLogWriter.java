package org.logl.logl;


import java.util.function.Consumer;
import java.util.function.Supplier;

import org.logl.Level;
import org.logl.LogMessage;
import org.logl.LogWriter;

final class LevelLogWriter implements LogWriter {
  private final Level level;
  private final LevelLogger logger;

  LevelLogWriter(Level level, LevelLogger logger) {
    this.level = level;
    this.logger = logger;
  }

  @Override
  public void log(LogMessage message) {
    logger.log(level, message);
  }

  @Override
  public void log(CharSequence message) {
    logger.log(level, message);
  }

  @Override
  public void log(Supplier<? extends CharSequence> messageSupplier) {
    logger.log(level, messageSupplier);
  }

  @Override
  public void log(LogMessage message, Throwable cause) {
    logger.log(level, message, cause);
  }

  @Override
  public void log(CharSequence message, Throwable cause) {
    logger.log(level, message, cause);
  }

  @Override
  public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    logger.log(level, messageSupplier, cause);
  }

  @Override
  public void logf(String format, Object... args) {
    logger.logf(level, format, args);
  }

  @Override
  public void batch(Consumer<LogWriter> fn) {
    logger.batch(level, fn);
  }
}

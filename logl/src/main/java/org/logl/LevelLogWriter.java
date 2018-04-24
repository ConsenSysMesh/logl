package org.logl;


import java.util.function.Consumer;

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
  public void log(LogMessage message, Throwable cause) {
    logger.log(level, message, cause);
  }

  @Override
  public void log(CharSequence message, Throwable cause) {
    logger.log(level, message, cause);
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

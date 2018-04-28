package org.logl.jul;

import static org.logl.LoggerProvider.loggerName;

import java.util.function.Function;

import org.logl.Logger;
import org.logl.LoggerProvider;

/**
 * An implementation of a {@link org.logl.LoggerProvider} that delegates to {@link java.util.logging.LogManager}.
 */
public class JULLoggerProvider implements LoggerProvider {
  private Function<String, java.util.logging.Logger> loggerFactory;

  /**
   * Create a logger provider that uses {@link java.util.logging.Logger#getLogger(String)} for obtaining a logger.
   */
  public JULLoggerProvider() {
    this(java.util.logging.Logger::getLogger);
  }

  /**
   * @param loggerFactory A function for obtaining {@code java.util.logging} loggers.
   */
  public JULLoggerProvider(Function<String, java.util.logging.Logger> loggerFactory) {
    this.loggerFactory = loggerFactory;
  }

  @Override
  public Logger getLogger(Class<?> loggingClass) {
    return new JULLogger(loggerFactory.apply(loggerName(loggingClass)));
  }

  @Override
  public Logger getLogger(String name) {
    return new JULLogger(loggerFactory.apply(name));
  }
}

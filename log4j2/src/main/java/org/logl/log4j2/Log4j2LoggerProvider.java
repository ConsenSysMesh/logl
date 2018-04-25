package org.logl.log4j2;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.logl.Logger;
import org.logl.LoggerProvider;

/**
 * An implementation of a {@link org.logl.LoggerProvider} that delegates to {@link org.apache.logging.log4j.LogManager}.
 */
public class Log4j2LoggerProvider implements LoggerProvider {
  private Function<String, org.apache.logging.log4j.Logger> loggerFactory;

  /**
   * Use the default log4j2 {@code LogManager}.
   */
  public Log4j2LoggerProvider() {
    this(LogManager::getLogger);
  }

  /**
   * @param loggerFactory A function for obtaining log4j2 loggers.
   */
  public Log4j2LoggerProvider(Function<String, org.apache.logging.log4j.Logger> loggerFactory) {
    this.loggerFactory = loggerFactory;
  }

  @Override
  public Logger getLogger(Class<?> loggingClass) {
    return new Log4j2Logger(loggerFactory.apply(loggingClass.getName()));
  }

  @Override
  public Logger getLogger(String name) {
    return new Log4j2Logger(loggerFactory.apply(name));
  }
}

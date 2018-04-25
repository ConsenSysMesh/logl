package org.logl.slf4j;

import java.util.function.Function;

import org.logl.Logger;
import org.logl.LoggerProvider;
import org.slf4j.LoggerFactory;

/**
 * An implementation of a {@link org.logl.LoggerProvider} that delegates to {@link org.slf4j.LoggerFactory}.
 */
public class Slf4jLoggerProvider implements LoggerProvider {
  private Function<String, org.slf4j.Logger> loggerFactory;

  /**
   * Use the default slf4j {@code LoggerFactory}.
   */
  public Slf4jLoggerProvider() {
    this(LoggerFactory::getLogger);
  }

  /**
   * @param loggerFactory A function for obtaining slf4j loggers.
   */
  public Slf4jLoggerProvider(Function<String, org.slf4j.Logger> loggerFactory) {
    this.loggerFactory = loggerFactory;
  }

  @Override
  public Logger getLogger(Class<?> loggingClass) {
    return new Slf4jLogger(loggerFactory.apply(loggingClass.getName()));
  }

  @Override
  public Logger getLogger(String name) {
    return new Slf4jLogger(loggerFactory.apply(name));
  }
}

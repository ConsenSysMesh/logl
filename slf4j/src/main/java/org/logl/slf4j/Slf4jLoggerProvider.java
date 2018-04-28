package org.logl.slf4j;

import static org.logl.LoggerProvider.loggerName;

import java.util.function.Function;

import org.logl.Logger;
import org.logl.LoggerProvider;
import org.slf4j.LoggerFactory;

/**
 * An implementation of a {@link org.logl.LoggerProvider} that delegates to {@link org.slf4j.LoggerFactory
 * org.slf4j.LoggerFactory}.
 */
public class Slf4jLoggerProvider implements LoggerProvider {
  private Function<String, org.slf4j.Logger> loggerFactory;

  /**
   * Create a logger provider that uses {@link LoggerFactory#getLogger(String)} for obtaining a logger.
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
    return new Slf4jLogger(loggerFactory.apply(loggerName(loggingClass)));
  }

  @Override
  public Logger getLogger(String name) {
    return new Slf4jLogger(loggerFactory.apply(name));
  }
}

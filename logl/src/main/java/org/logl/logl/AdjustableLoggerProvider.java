package org.logl.logl;

import static org.logl.LoggerProvider.loggerName;

import org.logl.LoggerProvider;

/**
 * A {@link LoggerProvider} that provides {@link AdjustableLogger} instances.
 */
public interface AdjustableLoggerProvider extends LoggerProvider {

  @Override
  default AdjustableLogger getLogger(Class<?> loggingClass) {
    return getLogger(loggerName(loggingClass));
  }

  @Override
  AdjustableLogger getLogger(String name);
}

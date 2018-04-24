package org.logl;

/**
 * A {@link LoggerProvider} that provides {@link AdjustableLogger} instances.
 */
public interface AdjustableLoggerProvider extends LoggerProvider {

  @Override
  default AdjustableLogger getLogger(Class<?> loggingClass) {
    return getLogger(loggingClass.getName());
  }

  @Override
  AdjustableLogger getLogger(String name);
}

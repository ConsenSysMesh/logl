package org.logl;

/**
 * A provider for {@link Logger} instances.
 */
public interface LoggerProvider {

  /**
   * @return A {@link LoggerProvider} that returns {@link Logger} instances that discards all log messages.
   */
  static LoggerProvider nullProvider() {
    return NullLoggerProvider.instance();
  }

  /**
   * Obtain a logger for a given class (by name).
   *
   * @param loggingClass The class to obtain a {@link Logger} for.
   * @return A {@link Logger} instance for the class (by name).
   */
  default Logger getLogger(Class<?> loggingClass) {
    return getLogger(loggingClass.getName());
  }

  /**
   * Obtain a logger for a specified name.
   *
   * @param name The name to obtain a {@link Logger} for.
   * @return A {@link Logger} instance for the specified name.
   */
  Logger getLogger(String name);
}

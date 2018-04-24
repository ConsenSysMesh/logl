package org.logl;

final class NullLoggerProvider implements LoggerProvider {
  private static final NullLoggerProvider INSTANCE = new NullLoggerProvider();

  static NullLoggerProvider instance() {
    return INSTANCE;
  }

  private NullLoggerProvider() {}

  @Override
  public Logger getLogger(Class<?> loggingClass) {
    return NullLogger.instance();
  }

  @Override
  public Logger getLogger(String name) {
    return NullLogger.instance();
  }
}

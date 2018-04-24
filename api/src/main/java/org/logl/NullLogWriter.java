package org.logl;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

final class NullLogWriter implements LogWriter {
  private static final NullLogWriter INSTANCE = new NullLogWriter();

  static NullLogWriter instance() {
    return INSTANCE;
  }

  private NullLogWriter() {}

  @Override
  public void log(LogMessage message) {
    requireNonNull(message);
  }

  @Override
  public void log(CharSequence message) {
    requireNonNull(message);
  }

  @Override
  public void log(LogMessage message, Throwable cause) {
    requireNonNull(message);
  }

  @Override
  public void log(CharSequence message, Throwable cause) {
    requireNonNull(message);
  }

  @Override
  public void logf(String format, Object... args) {
    requireNonNull(format);
  }

  @Override
  public void batch(Consumer<LogWriter> fn) {
    requireNonNull(fn);
    fn.accept(this);
  }
}

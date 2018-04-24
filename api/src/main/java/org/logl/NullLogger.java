package org.logl;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

final class NullLogger implements Logger {
  private static final NullLogger INSTANCE = new NullLogger();

  static NullLogger instance() {
    return INSTANCE;
  }

  private NullLogger() {}

  private LogWriter logWriter = NullLogWriter.instance();

  @Override
  public Level getLevel() {
    return Level.NONE;
  }

  @Override
  public boolean isEnabled(Level level) {
    requireNonNull(level);
    return false;
  }

  @Override
  public LogWriter errorWriter() {
    return logWriter;
  }

  @Override
  public LogWriter warnWriter() {
    return logWriter;
  }

  @Override
  public LogWriter infoWriter() {
    return logWriter;
  }

  @Override
  public LogWriter debugWriter() {
    return logWriter;
  }

  @Override
  public void batch(Consumer<Logger> fn) {
    requireNonNull(fn);
    fn.accept(this);
  }
}

package org.logl.logl;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.logl.Level;
import org.logl.LogMessage;
import org.logl.LogWriter;
import org.logl.Logger;
import org.logl.logl.SimpleLogger.Builder;

final class SimpleLoggerImpl implements AdjustableLogger, LevelLogger {

  private final String name;
  private final AtomicReference<Level> level;
  private final Supplier<Instant> currentTimeSupplier;
  private final ZoneId zone;
  private final Locale locale;
  private final boolean autoFlush;
  private final Supplier<PrintWriter> writerSupplier;

  private final LevelLogWriter errorWriter;
  private final LevelLogWriter warnWriter;
  private final LevelLogWriter infoWriter;
  private final LevelLogWriter debugWriter;

  SimpleLoggerImpl(String name, Builder builder, Supplier<PrintWriter> writerSupplier) {
    this.name = NameAbbreviator.forPattern("1.").abbreviate(name);
    this.level = new AtomicReference<>(builder.level);
    this.currentTimeSupplier = builder.currentTimeSupplier;
    this.zone = builder.zone;
    this.locale = builder.locale;
    this.autoFlush = builder.autoFlush;
    this.writerSupplier = writerSupplier;

    this.errorWriter = new LevelLogWriter(Level.ERROR, this);
    this.warnWriter = new LevelLogWriter(Level.WARN, this);
    this.infoWriter = new LevelLogWriter(Level.INFO, this);
    this.debugWriter = new LevelLogWriter(Level.DEBUG, this);
  }

  @Override
  public Level getLevel() {
    return this.level.get();
  }

  @Override
  public Level setLevel(Level level) {
    requireNonNull(level);
    return this.level.getAndSet(level);
  }

  @Override
  public void error(LogMessage message) {
    log(Level.ERROR, message);
  }

  @Override
  public void error(CharSequence message) {
    log(Level.ERROR, message);
  }

  @Override
  public void error(Supplier<? extends CharSequence> messageSupplier) {
    log(Level.ERROR, messageSupplier);
  }

  @Override
  public void error(LogMessage message, Throwable cause) {
    log(Level.ERROR, message, cause);
  }

  @Override
  public void error(CharSequence message, Throwable cause) {
    log(Level.ERROR, message, cause);
  }

  @Override
  public void error(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    log(Level.ERROR, messageSupplier, cause);
  }

  @Override
  public void error(String pattern, Object... args) {
    log(Level.ERROR, pattern, args);
  }

  @Override
  public LogWriter errorWriter() {
    return this.errorWriter;
  }

  @Override
  public void warn(LogMessage message) {
    log(Level.WARN, message);
  }

  @Override
  public void warn(CharSequence message) {
    log(Level.WARN, message);
  }

  @Override
  public void warn(Supplier<? extends CharSequence> messageSupplier) {
    log(Level.WARN, messageSupplier);
  }

  @Override
  public void warn(LogMessage message, Throwable cause) {
    log(Level.WARN, message, cause);
  }

  @Override
  public void warn(CharSequence message, Throwable cause) {
    log(Level.WARN, message, cause);
  }

  @Override
  public void warn(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    log(Level.WARN, messageSupplier, cause);
  }

  @Override
  public void warn(String pattern, Object... args) {
    log(Level.WARN, pattern, args);
  }

  @Override
  public LogWriter warnWriter() {
    return this.warnWriter;
  }

  @Override
  public void info(LogMessage message) {
    log(Level.INFO, message);
  }

  @Override
  public void info(CharSequence message) {
    log(Level.INFO, message);
  }

  @Override
  public void info(Supplier<? extends CharSequence> messageSupplier) {
    log(Level.INFO, messageSupplier);
  }

  @Override
  public void info(LogMessage message, Throwable cause) {
    log(Level.INFO, message, cause);
  }

  @Override
  public void info(CharSequence message, Throwable cause) {
    log(Level.INFO, message, cause);
  }

  @Override
  public void info(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    log(Level.INFO, messageSupplier, cause);
  }

  @Override
  public void info(String pattern, Object... args) {
    log(Level.INFO, pattern, args);
  }

  @Override
  public LogWriter infoWriter() {
    return this.infoWriter;
  }

  @Override
  public void debug(LogMessage message) {
    log(Level.DEBUG, message);
  }

  @Override
  public void debug(CharSequence message) {
    log(Level.DEBUG, message);
  }

  @Override
  public void debug(Supplier<? extends CharSequence> messageSupplier) {
    log(Level.DEBUG, messageSupplier);
  }

  @Override
  public void debug(LogMessage message, Throwable cause) {
    log(Level.DEBUG, message, cause);
  }

  @Override
  public void debug(CharSequence message, Throwable cause) {
    log(Level.DEBUG, message, cause);
  }

  @Override
  public void debug(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    log(Level.DEBUG, messageSupplier, cause);
  }

  @Override
  public void debug(String pattern, Object... args) {
    log(Level.DEBUG, pattern, args);
  }

  @Override
  public LogWriter debugWriter() {
    return this.debugWriter;
  }

  @Override
  public void log(Level level, LogMessage message) {
    requireNonNull(message);
    if (level.compareTo(this.level.get()) > 0) {
      return;
    }
    Instant now = currentTimeSupplier.get();
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      writePrefix(out, now, level);
      writeMessage(out, message);
      out.println();
    }
  }

  @Override
  public void log(Level level, CharSequence message) {
    requireNonNull(message);
    if (level.compareTo(this.level.get()) > 0) {
      return;
    }
    Instant now = currentTimeSupplier.get();
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      writePrefix(out, now, level);
      out.print(message);
      out.println();
    }
  }

  @Override
  public void log(Level level, Supplier<? extends CharSequence> messageSupplier) {
    requireNonNull(messageSupplier);
    if (level.compareTo(this.level.get()) > 0) {
      return;
    }
    CharSequence message = messageSupplier.get();
    Instant now = currentTimeSupplier.get();
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      writePrefix(out, now, level);
      out.print(message);
      out.println();
    }
  }

  @Override
  public void log(Level level, LogMessage message, Throwable cause) {
    if (cause == null) {
      log(level, message);
      return;
    }
    requireNonNull(message);
    if (level.compareTo(this.level.get()) > 0) {
      return;
    }
    Instant now = currentTimeSupplier.get();
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      writePrefix(out, now, level);
      writeMessage(out, message);
      out.println();
      cause.printStackTrace(out);
    }
  }

  @Override
  public void log(Level level, CharSequence message, Throwable cause) {
    if (cause == null) {
      log(level, message);
      return;
    }
    requireNonNull(message);
    if (level.compareTo(this.level.get()) > 0) {
      return;
    }
    Instant now = currentTimeSupplier.get();
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      writePrefix(out, now, level);
      out.print(message);
      out.println();
      cause.printStackTrace(out);
    }
  }

  @Override
  public void log(Level level, Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    if (cause == null) {
      log(level, messageSupplier);
      return;
    }
    requireNonNull(messageSupplier);
    if (level.compareTo(this.level.get()) > 0) {
      return;
    }
    CharSequence message = messageSupplier.get();
    Instant now = currentTimeSupplier.get();
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      writePrefix(out, now, level);
      out.print(message);
      out.println();
      cause.printStackTrace(out);
    }
  }

  @Override
  public void logf(Level level, String format, Object... args) {
    requireNonNull(format);
    if (level.compareTo(this.level.get()) > 0) {
      return;
    }
    Instant now = currentTimeSupplier.get();
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      writePrefix(out, now, level);
      out.printf(format, args);
      out.println();
    }
  }

  @Override
  public void batch(Consumer<Logger> fn) {
    requireNonNull(fn);
    List<LogEvent> events = new ArrayList<>(32);
    fn.accept(new BatchLogger(events::add));
    writeEvents(events);
  }

  @Override
  public void batch(Level level, Consumer<LogWriter> fn) {
    requireNonNull(fn);
    List<LogEvent> events = new ArrayList<>(32);
    fn.accept(new BatchLogWriter(level, events::add));
    writeEvents(events);
  }

  private static final class LogEvent {
    final Instant time;
    final Level level;
    final CharSequence formattedMessage;
    final LogMessage message;
    final Throwable cause;

    LogEvent(Instant time, Level level, CharSequence formattedMessage, Throwable cause) {
      this.time = time;
      this.level = level;
      this.formattedMessage = formattedMessage;
      this.message = null;
      this.cause = cause;
    }

    LogEvent(Instant time, Level level, LogMessage message, Throwable cause) {
      this.time = time;
      this.level = level;
      this.formattedMessage = null;
      this.message = message;
      this.cause = cause;
    }
  }

  private final class BatchLogger implements Logger {
    private final LogWriter errorWriter;
    private final LogWriter warnWriter;
    private final LogWriter infoWriter;
    private final LogWriter debugWriter;

    BatchLogger(Consumer<LogEvent> eventConsumer) {
      this.errorWriter =
          isEnabled(Level.ERROR) ? new BatchLogWriter(Level.ERROR, eventConsumer) : LogWriter.nullWriter();
      this.warnWriter = isEnabled(Level.WARN) ? new BatchLogWriter(Level.WARN, eventConsumer) : LogWriter.nullWriter();
      this.infoWriter = isEnabled(Level.INFO) ? new BatchLogWriter(Level.INFO, eventConsumer) : LogWriter.nullWriter();
      this.debugWriter =
          isEnabled(Level.DEBUG) ? new BatchLogWriter(Level.DEBUG, eventConsumer) : LogWriter.nullWriter();
    }

    @Override
    public Level getLevel() {
      return Level.DEBUG;
    }

    @Override
    public boolean isEnabled(Level level) {
      requireNonNull(level);
      return true;
    }

    @Override
    public LogWriter errorWriter() {
      return this.errorWriter;
    }

    @Override
    public LogWriter warnWriter() {
      return this.warnWriter;
    }

    @Override
    public LogWriter infoWriter() {
      return this.infoWriter;
    }

    @Override
    public LogWriter debugWriter() {
      return this.debugWriter;
    }

    @Override
    public void batch(Consumer<Logger> fn) {
      requireNonNull(fn);
      fn.accept(this);
    }
  }

  private final class BatchLogWriter implements LogWriter {
    private final Level level;
    private final Consumer<LogEvent> eventConsumer;

    BatchLogWriter(Level level, Consumer<LogEvent> eventConsumer) {
      this.level = level;
      this.eventConsumer = eventConsumer;
    }

    @Override
    public void log(LogMessage message) {
      requireNonNull(message);
      eventConsumer.accept(new LogEvent(currentTimeSupplier.get(), level, message, null));
    }

    @Override
    public void log(CharSequence message) {
      requireNonNull(message);
      eventConsumer.accept(new LogEvent(currentTimeSupplier.get(), level, message, null));
    }

    @Override
    public void log(LogMessage message, Throwable cause) {
      requireNonNull(message);
      eventConsumer.accept(new LogEvent(currentTimeSupplier.get(), level, message, cause));
    }

    @Override
    public void log(CharSequence message, Throwable cause) {
      requireNonNull(message);
      eventConsumer.accept(new LogEvent(currentTimeSupplier.get(), level, message, cause));
    }

    @Override
    public void batch(Consumer<LogWriter> fn) {
      requireNonNull(fn);
      fn.accept(this);
    }
  }

  private void writeEvents(Collection<LogEvent> logEvents) {
    if (logEvents.isEmpty()) {
      return;
    }
    Level currentLevel = this.level.get();
    PrintWriter out;
    synchronized (this) {
      out = writerSupplier.get();
      for (LogEvent logEvent : logEvents) {
        Level level = logEvent.level;
        if (level.compareTo(currentLevel) > 0) {
          continue;
        }

        writePrefix(out, logEvent.time, level);

        LogMessage message = logEvent.message;
        if (message != null) {
          writeMessage(out, message);
        } else {
          out.print(logEvent.formattedMessage);
        }
        out.println();

        Throwable cause = logEvent.cause;
        if (cause != null) {
          cause.printStackTrace(out);
        }
      }
    }
    if (autoFlush) {
      out.flush();
    }
  }

  private void writePrefix(PrintWriter out, Instant now, Level level) {
    DateFormatter.formatTo(now.atZone(zone), out);
    String lname = level.name();
    out.write("  ", 0, 6 - lname.length());
    out.write(lname);
    out.write(" [");
    out.write(name);
    out.write("] ");
  }

  private void writeMessage(PrintWriter out, LogMessage message) {
    try {
      message.appendTo(locale, out);
    } catch (IOException ex) {
      // PrintWriter does not throw this exception
      throw new RuntimeException("unexpected exception", ex);
    }
  }
}

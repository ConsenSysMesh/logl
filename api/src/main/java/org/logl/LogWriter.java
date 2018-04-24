package org.logl;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A logl {@code LogWriter}.
 *
 * <p>
 * This interface provides all the common logging methods used at runtime.
 */
public interface LogWriter {

  /**
   * @return A {@link LogWriter} that returns {@link Logger} instances that discards all log messages.
   */
  static LogWriter nullWriter() {
    return NullLogWriter.instance();
  }

  /**
   * Log a {@link LogMessage}.
   *
   * @param message The message to log.
   */
  void log(LogMessage message);

  /**
   * Log a message.
   *
   * @param message A character sequence (or {@link String}).
   */
  default void log(CharSequence message) {
    requireNonNull(message);
    log((l, a) -> a.append(message));
  }

  /**
   * Log the supplied message.
   *
   * The supplier will only be invoked if the log will be written. Use this method to avoid expensive string
   * construction when unnecessary.
   *
   * @param messageSupplier A {@link Supplier} for the message.
   */
  default void log(Supplier<? extends CharSequence> messageSupplier) {
    requireNonNull(messageSupplier);
    log((l, a) -> a.append(messageSupplier.get()));
  }

  /**
   * Log a message and exception.
   *
   * @param message The message to log.
   * @param cause The exception to log.
   */
  void log(LogMessage message, Throwable cause);

  /**
   * Log a message and exception at level.
   *
   * @param message A character sequence (or {@link String}).
   * @param cause The exception to log.
   */
  default void log(CharSequence message, Throwable cause) {
    requireNonNull(message);
    log((l, a) -> a.append(message), cause);
  }

  /**
   * Log the supplied message and exception.
   *
   * The supplier will only be invoked if the message will be written to the log. Use this method to avoid expensive
   * string construction when unnecessary.
   *
   * @param messageSupplier A {@link Supplier} for the message.
   * @param cause The exception to log.
   */
  default void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    requireNonNull(messageSupplier);
    log((l, a) -> a.append(messageSupplier.get()), cause);
  }

  /**
   * Log a pattern formatted message.
   *
   * This defaults to {@code log(LogMessage.patternFormat(pattern, args))}, but some logging implementations may provide
   * a more efficient alternative.
   *
   * @param pattern The format string, as per {@link LogMessage#patternFormat(String, Object...)}.
   * @param args The format arguments.
   */
  default void log(String pattern, Object... args) {
    if (args.length == 1 && args[0] instanceof Throwable) {
      log(pattern, (Throwable) args[0]);
    } else {
      log(LogMessage.patternFormat(pattern, args));
    }
  }

  /**
   * Log a string formatted message.
   *
   * This defaults to {@code log(LogMessage.stringFormat(format, args))}, but some logging implementations may provide a
   * more efficient alternative.
   *
   * @param format The format string, as per {@link LogMessage#stringFormat(String, Object...)}.
   * @param args The format arguments.
   */
  default void logf(String format, Object... args) {
    log(LogMessage.stringFormat(format, args));
  }

  /**
   * Write a set of log messages in a batch.
   *
   * <p>
   * The specified {@link Consumer} will be invoked with a {@link LogWriter} that collects all log output and then
   * writes them all consecutively when the consumer invocation returns. Use this when a set of log lines must appear
   * consecutively in the log, regardless of time ordering.
   * <p>
   * Note: most implementations do not guarantee log ordering, so log lines may not always be ordered correctly by
   * timestamp. This is especially true when logs are written in batch.
   *
   * @param fn A {@link Consumer} that will be invoked with the batching {@link LogWriter}.
   */
  void batch(Consumer<LogWriter> fn);
}

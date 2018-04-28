package org.logl.logl;

import static java.util.Objects.requireNonNull;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import org.logl.Level;
import org.logl.Logger;

/**
 * Factory methods for creating loggers that writes log lines using a common log format.
 */
public final class SimpleLogger {
  private SimpleLogger() {}

  /**
   * Start building a simple logger that uses the specified timezone for timestamps.
   *
   * @param timeZone The timezone to use for timestamps.
   * @return A builder for a simple logger.
   */
  public static Builder withZone(ZoneId timeZone) {
    return new Builder().withZone(timeZone);
  }

  /**
   * Start building a simple logger that uses the specified locale for message output.
   *
   * @param locale The locale to use for message output.
   * @return A builder for a simple logger.
   */
  public static Builder withLocale(Locale locale) {
    return new Builder().withLocale(locale);
  }

  /**
   * Start building a simple logger that writes log lines at or above the specified level.
   *
   * @param level The level at or above which log lines will be output.
   * @return A builder for a simple logger.
   */
  public static Builder withLogLevel(Level level) {
    return new Builder().withLogLevel(level);
  }

  /**
   * Start building a simple logger that uses the specified supplier for timestamps.
   *
   * <p>
   * This method is exposed for use in testing, where it may be necessary to fix the timestamps created during logging.
   *
   * @param currentTimeSupplier A {@link Supplier} for the current time.
   * @return A builder for a simple logger.
   */
  static Builder usingCurrentTimeSupplier(Supplier<Instant> currentTimeSupplier) {
    return new Builder().usingCurrentTimeSupplier(currentTimeSupplier);
  }

  /**
   * Start building a simple logger that does not flush the output after each write.
   *
   * @return A builder for a simple logger.
   */
  public static Builder withoutAutoFlush() {
    return new Builder().withoutAutoFlush();
  }

  /**
   * Return a provider for simple loggers that write to the supplied {@link PrintWriter}.
   *
   * @param writer A {@link PrintWriter} to output log lines to.
   * @return A {@link AdjustableLoggerProvider} that provides {@link Logger} instances using a common log format.
   */
  public static AdjustableLoggerProvider toPrintWriter(PrintWriter writer) {
    return new Builder().toPrintWriter(writer);
  }

  /**
   * Return a provider for simple loggers that write to the supplied {@link OutputStream}.
   *
   * <p>
   * This convenience method creates the necessary intermediate OutputStreamWriter, which will convert characters into
   * bytes using the UTF-8 character encoding.
   *
   * @param stream An {@link OutputStream} to output log lines to.
   * @return A {@link AdjustableLoggerProvider} that provides {@link Logger} instances using a common log format.
   */
  public static AdjustableLoggerProvider toOutputStream(OutputStream stream) {
    return new Builder().toOutputStream(stream);
  }

  /**
   * Return a provider for simple loggers that write to the supplied {@link PrintWriter}.
   *
   * @param writerSupplier A {@link Supplier} for a {@link PrintWriter}, where log lines will be output to.
   * @return A {@link AdjustableLoggerProvider} that provides {@link Logger} instances using a common log format.
   */
  public static AdjustableLoggerProvider toPrintWriter(Supplier<PrintWriter> writerSupplier) {
    return new Builder().toPrintWriter(writerSupplier);
  }

  /**
   * A builder for a logger that uses a common log format.
   */
  public static class Builder {
    ZoneId zone = ZoneOffset.UTC;
    Locale locale = Locale.getDefault();
    Level level = Level.INFO;
    Supplier<Instant> currentTimeSupplier = Instant::now;
    boolean autoFlush = true;

    /**
     * Use the specified timezone for timestamps.
     *
     * @param timeZone The timezone to use for timestamps.
     * @return This builder.
     */
    public Builder withZone(ZoneId timeZone) {
      requireNonNull(timeZone);
      this.zone = timeZone;
      return this;
    }

    /**
     * Use the specified locale for message output.
     *
     * @param locale The locale to use for message output.
     * @return This builder.
     */
    public Builder withLocale(Locale locale) {
      requireNonNull(locale);
      this.locale = locale;
      return this;
    }

    /**
     * Write log lines at or above the specified level.
     *
     * @param level The level at or above which log lines will be output.
     * @return This builder.
     */
    public Builder withLogLevel(Level level) {
      requireNonNull(level);
      this.level = level;
      return this;
    }

    /**
     * Use the specified supplier for timestamps.
     *
     * <p>
     * This method is exposed for use in testing, where it may be necessary to fix the timestamps created during
     * logging.
     *
     * @param currentTimeSupplier A {@link Supplier} for the current time.
     * @return This builder.
     */
    public Builder usingCurrentTimeSupplier(Supplier<Instant> currentTimeSupplier) {
      requireNonNull(currentTimeSupplier);
      this.currentTimeSupplier = currentTimeSupplier;
      return this;
    }

    /**
     * Do not flush the output after each write.
     *
     * @return This builder.
     */
    public Builder withoutAutoFlush() {
      this.autoFlush = false;
      return this;
    }

    /**
     * Return a provider for simple loggers that write to the supplied {@link PrintWriter}.
     *
     * @param writer A {@link PrintWriter} to output log lines to.
     * @return A {@link AdjustableLoggerProvider} that provides {@link Logger} instances using a common log format.
     */
    public AdjustableLoggerProvider toPrintWriter(PrintWriter writer) {
      requireNonNull(writer);
      return toPrintWriter(() -> writer);
    }

    /**
     * Return a provider for simple loggers that write to the supplied {@link OutputStream}.
     *
     * <p>
     * This convenience method creates the necessary intermediate OutputStreamWriter, which will convert characters into
     * bytes using the UTF-8 character encoding.
     *
     * @param stream An {@link OutputStream} to output log lines to.
     * @return A {@link AdjustableLoggerProvider} that provides {@link Logger} instances using a common log format.
     */
    public AdjustableLoggerProvider toOutputStream(OutputStream stream) {
      requireNonNull(stream);
      PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8)));
      return toPrintWriter(() -> writer);
    }

    /**
     * Return a provider for simple loggers that write to the supplied {@link PrintWriter}.
     *
     * @param writerSupplier A {@link Supplier} for a {@link PrintWriter}, where log lines will be output to.
     * @return A {@link AdjustableLoggerProvider} that provides {@link Logger} instances using a common log format.
     */
    public AdjustableLoggerProvider toPrintWriter(Supplier<PrintWriter> writerSupplier) {
      requireNonNull(writerSupplier);
      return new Provider(this, writerSupplier);
    }
  }

  private static class Provider implements AdjustableLoggerProvider {
    private final Builder builder;
    private final Supplier<PrintWriter> writerSupplier;
    private final Map<String, SimpleLoggerImpl> loggers = Collections.synchronizedMap(new WeakValueHashMap<>());

    private Provider(Builder builder, Supplier<PrintWriter> writerSupplier) {
      this.builder = builder;
      this.writerSupplier = writerSupplier;
    }

    @Override
    public AdjustableLogger getLogger(String name) {
      return loggers.computeIfAbsent(name, n -> new SimpleLoggerImpl(n, builder, writerSupplier));
    }
  }
}

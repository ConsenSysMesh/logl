package org.logl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DuplicatingLoggerTest {

  private CharArrayWriter[] buffer;
  private PrintWriter[] out;
  private Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

  @BeforeEach
  void setup() {
    buffer = new CharArrayWriter[3];
    out = new PrintWriter[3];
    for (int i = 0; i < 3; ++i) {
      buffer[i] = new CharArrayWriter();
      out[i] = new PrintWriter(buffer[i]);
    }
  }

  @Test
  void shouldDuplicateOutput() {
    LoggerProvider logProvider = new DuplicatingLoggerProvider(
        UnformattedLogger.toPrintWriter(out[0]),
        SimpleLogger.usingCurrentTimeSupplier(() -> now).toPrintWriter(out[1]));
    Logger logger = logProvider.getLogger(getClass());

    logger.info("1output");
    now = now.plusMillis(700);
    logger.warn("{}{}", 2, "output");
    logger.debug("3output");
    // @formatter:off
    assertThat(buffer[0].toString()).isEqualTo(String.format(
        "1output%n" +
        "2output%n"));
    assertThat(buffer[1].toString()).isEqualTo(String.format(
        "2007-12-03 10:15:30.000+0000  INFO [o.l.DuplicatingLoggerTest] 1output%n" +
        "2007-12-03 10:15:30.700+0000  WARN [o.l.DuplicatingLoggerTest] 2output%n"));
    // @formatter:on
  }

  @Test
  void shouldRemoveLoggerProviderFromOutput() {
    LoggerProvider unformattedLoggerProvider = UnformattedLogger.toPrintWriter(out[0]);
    LoggerProvider simpleLoggerProvider = SimpleLogger.usingCurrentTimeSupplier(() -> now).toPrintWriter(out[1]);
    DuplicatingLoggerProvider logProvider =
        new DuplicatingLoggerProvider(unformattedLoggerProvider, simpleLoggerProvider);
    DuplicatingLogger logger = logProvider.getLogger(getClass());

    logger.info("1output");
    now = now.plusMillis(700);

    logProvider.removeProvider(simpleLoggerProvider);

    logger.warn("{}{}", 2, "output");
    logger.debug("3output");

    // @formatter:off
    assertThat(buffer[0].toString()).isEqualTo(String.format(
        "1output%n" +
            "2output%n"));
    assertThat(buffer[1].toString()).isEqualTo(String.format(
        "2007-12-03 10:15:30.000+0000  INFO [o.l.DuplicatingLoggerTest] 1output%n"));
    // @formatter:on
  }

  @Test
  void shouldRemoveLoggerFromOutput() {
    LoggerProvider unformattedLoggerProvider = UnformattedLogger.toPrintWriter(out[0]);
    LoggerProvider simpleLoggerProvider = SimpleLogger.usingCurrentTimeSupplier(() -> now).toPrintWriter(out[1]);
    DuplicatingLoggerProvider logProvider =
        new DuplicatingLoggerProvider(unformattedLoggerProvider, simpleLoggerProvider);
    DuplicatingLogger logger = logProvider.getLogger(getClass());

    logger.info("1output");
    now = now.plusMillis(700);

    logger.removeLogger(simpleLoggerProvider.getLogger(getClass()));

    logger.warn("{}{}", 2, "output");
    logger.debug("3output");

    // @formatter:off
    assertThat(buffer[0].toString()).isEqualTo(String.format(
        "1output%n" +
        "2output%n"));
    assertThat(buffer[1].toString()).isEqualTo(String.format(
        "2007-12-03 10:15:30.000+0000  INFO [o.l.DuplicatingLoggerTest] 1output%n"));
    // @formatter:on
  }
}

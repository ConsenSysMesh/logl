/*
 * Copyright 2018 ConsenSys AG.
 *
 * This code is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files(the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and / or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.logl.logl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

import org.assertj.core.api.Java6Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logl.Level;
import org.logl.LogMessage;
import org.logl.Logger;
import org.logl.LoggerProvider;

class DuplicatingLoggerTest {

  private Instant now = Instant.parse("2007-12-03T10:15:30.00Z");
  private StringWriter stringWriter = new StringWriter();
  private DuplicatingLoggerProvider loggerProvider = new DuplicatingLoggerProvider(
      SimpleLogger.withLogLevel(Level.DEBUG).usingCurrentTimeSupplier(() -> now).toPrintWriter(
          new PrintWriter(stringWriter)));

  private CharArrayWriter[] buffer;
  private PrintWriter[] out;

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
        "2007-12-03 10:15:30.000+0000  INFO [o.l.l.DuplicatingLoggerTest] 1output%n" +
        "2007-12-03 10:15:30.700+0000  WARN [o.l.l.DuplicatingLoggerTest] 2output%n"));
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
        "2007-12-03 10:15:30.000+0000  INFO [o.l.l.DuplicatingLoggerTest] 1output%n"));
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
        "2007-12-03 10:15:30.000+0000  INFO [o.l.l.DuplicatingLoggerTest] 1output%n"));
    // @formatter:on
  }

  @Test
  void shouldLogMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error("Logging logl->duplog->buffer");
    logger.warn("Logging logl->duplog->buffer");
    logger.debug("Logging logl->duplog->buffer");
    logger.info("Logging logl->duplog->buffer");
    // @formatter:off
    Java6Assertions.assertThat(stringWriter.toString()).isEqualTo(String.format(
      "2007-12-03 10:15:30.000+0000 ERROR [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
        "2007-12-03 10:15:30.000+0000  WARN [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
        "2007-12-03 10:15:30.000+0000 DEBUG [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
        "2007-12-03 10:15:30.000+0000  INFO [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n"));
    // @formatter:on
  }

  @Test
  void shouldLogMessageLog() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error(LogMessage.stringFormat("Logging logl->duplog->%s", "buffer"));
    logger.warn(LogMessage.stringFormat("Logging logl->duplog->%s", "buffer"));
    logger.info(LogMessage.stringFormat("Logging logl->duplog->%s", "buffer"));
    logger.debug(LogMessage.stringFormat("Logging logl->duplog->%s", "buffer"));
    // @formatter:off
    Java6Assertions.assertThat(stringWriter.toString()).isEqualTo(String.format(
      "2007-12-03 10:15:30.000+0000 ERROR [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
      "2007-12-03 10:15:30.000+0000  WARN [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
      "2007-12-03 10:15:30.000+0000  INFO [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
      "2007-12-03 10:15:30.000+0000 DEBUG [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n"));
    // @formatter:on
  }

  @Test
  void shouldLogMessageAndCause() {
    Logger logger = loggerProvider.getLogger(getClass());
    RuntimeException cause = new RuntimeException("Something happened");
    StackTraceElement element1 = new StackTraceElement("org.logl.DuplicatingLoggerTest", "foo", "foo.java", 63);
    StackTraceElement element2 = new StackTraceElement("org.logl.DuplicatingLoggerTest", "bar", "foo.java", 32);
    StackTraceElement[] stackTrace = new StackTraceElement[] {element1, element2};
    cause.setStackTrace(stackTrace);

    logger.info("Logging logl->duplog->buffer", cause);
    logger.debug("Logging logl->duplog->buffer", cause);
    logger.warn("Logging logl->duplog->buffer", cause);
    logger.error("Logging logl->duplog->buffer", cause);

    // @formatter:off
    Java6Assertions.assertThat(stringWriter.toString()).isEqualTo(String.format(
      "2007-12-03 10:15:30.000+0000  INFO [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.DuplicatingLoggerTest.foo(foo.java:63)%n" +
        "\tat org.logl.DuplicatingLoggerTest.bar(foo.java:32)%n" +
        "2007-12-03 10:15:30.000+0000 DEBUG [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.DuplicatingLoggerTest.foo(foo.java:63)%n" +
        "\tat org.logl.DuplicatingLoggerTest.bar(foo.java:32)%n" +
        "2007-12-03 10:15:30.000+0000  WARN [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.DuplicatingLoggerTest.foo(foo.java:63)%n" +
        "\tat org.logl.DuplicatingLoggerTest.bar(foo.java:32)%n" +
        "2007-12-03 10:15:30.000+0000 ERROR [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.DuplicatingLoggerTest.foo(foo.java:63)%n" +
        "\tat org.logl.DuplicatingLoggerTest.bar(foo.java:32)%n"));
    // @formatter:on
  }

  @Test
  void shouldLogSuppliedMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error(() -> "Logging logl->duplog->buffer");
    logger.warn(() -> "Logging logl->duplog->buffer");
    logger.debug(() -> "Logging logl->duplog->buffer");
    logger.info(() -> "Logging logl->duplog->buffer");
    // @formatter:off
    Java6Assertions.assertThat(stringWriter.toString()).isEqualTo(String.format(
      "2007-12-03 10:15:30.000+0000 ERROR [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
      "2007-12-03 10:15:30.000+0000  WARN [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
      "2007-12-03 10:15:30.000+0000 DEBUG [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
      "2007-12-03 10:15:30.000+0000  INFO [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n"));
    // @formatter:on
  }

  @Test
  void shouldLogSuppliedMessageAndCause() {
    Logger logger = loggerProvider.getLogger(getClass());
    RuntimeException cause = new RuntimeException("Something happened");
    StackTraceElement element1 = new StackTraceElement("org.logl.DuplicatingLoggerTest", "foo", "foo.java", 63);
    StackTraceElement element2 = new StackTraceElement("org.logl.DuplicatingLoggerTest", "bar", "foo.java", 32);
    StackTraceElement[] stackTrace = new StackTraceElement[] {element1, element2};
    cause.setStackTrace(stackTrace);

    logger.info(() -> "Logging logl->duplog->buffer", cause);
    logger.debug(() -> "Logging logl->duplog->buffer", cause);
    logger.warn(() -> "Logging logl->duplog->buffer", cause);
    logger.error(() -> "Logging logl->duplog->buffer", cause);

    // @formatter:off
    Java6Assertions.assertThat(stringWriter.toString()).isEqualTo(String.format(
      "2007-12-03 10:15:30.000+0000  INFO [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.DuplicatingLoggerTest.foo(foo.java:63)%n" +
        "\tat org.logl.DuplicatingLoggerTest.bar(foo.java:32)%n" +
        "2007-12-03 10:15:30.000+0000 DEBUG [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.DuplicatingLoggerTest.foo(foo.java:63)%n" +
        "\tat org.logl.DuplicatingLoggerTest.bar(foo.java:32)%n" +
        "2007-12-03 10:15:30.000+0000  WARN [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.DuplicatingLoggerTest.foo(foo.java:63)%n" +
        "\tat org.logl.DuplicatingLoggerTest.bar(foo.java:32)%n" +
        "2007-12-03 10:15:30.000+0000 ERROR [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.DuplicatingLoggerTest.foo(foo.java:63)%n" +
        "\tat org.logl.DuplicatingLoggerTest.bar(foo.java:32)%n"));
    // @formatter:on
  }

  @Test
  void shouldLogFormattedErrorMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error("Logging {}->{}->{}", "logl", "duplog", "buffer");
    logger.warnf("Logging %s->%s->%s", "logl", "duplog", "buffer");
    logger.info("Logging {}->{}->{}", "logl", "duplog", "buffer");
    logger.debugf("Logging %s->%s->%s", "logl", "duplog", "buffer");
    // @formatter:off
    Java6Assertions.assertThat(stringWriter.toString()).isEqualTo(String.format(
      "2007-12-03 10:15:30.000+0000 ERROR [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
      "2007-12-03 10:15:30.000+0000  WARN [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
      "2007-12-03 10:15:30.000+0000  INFO [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n" +
      "2007-12-03 10:15:30.000+0000 DEBUG [o.l.l.DuplicatingLoggerTest] Logging logl->duplog->buffer%n"));
    // @formatter:on
  }

  @Test
  void levelShouldBeDebug() {
    Logger logger = loggerProvider.getLogger(getClass());
    assertEquals(Level.DEBUG, logger.getLevel());
  }

  @Test
  void levelEnabled() {
    Logger logger = loggerProvider.getLogger(getClass());
    assertTrue(logger.isEnabled(Level.DEBUG));
    assertTrue(logger.isEnabled(Level.INFO));
    assertTrue(logger.isEnabled(Level.WARN));
    assertTrue(logger.isEnabled(Level.ERROR));
  }
}

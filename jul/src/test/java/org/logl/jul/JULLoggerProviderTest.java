package org.logl.jul;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.logging.LogManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logl.Logger;

class JULLoggerProviderTest {

  private JULLoggerProvider loggerProvider;
  private StringWriterHandler handler;

  @BeforeEach
  void setup() {
    handler = new StringWriterHandler();

    LogManager logManager = LogManager.getLogManager();
    logManager.reset();
    java.util.logging.Logger rootLogger = logManager.getLogger("");
    rootLogger.addHandler(handler);

    loggerProvider = new JULLoggerProvider();
  }

  @Test
  void shouldLogMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error("Logging logl->jul->buffer");
    logger.warn("Logging logl->jul->buffer");
    logger.debug("Logging logl->jul->buffer");
    logger.info("Logging logl->jul->buffer");
    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "SEVERE  [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n" +
        "WARNING [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n" +
        "INFO    [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n"));
    // @formatter:on
  }

  @Test
  void shouldLogMessageAndCause() {
    Logger logger = loggerProvider.getLogger(getClass());
    RuntimeException cause = new RuntimeException("Something happened");
    StackTraceElement element1 = new StackTraceElement("org.logl.jul.JULLoggerProviderTest", "foo", "foo.java", 63);
    StackTraceElement element2 = new StackTraceElement("org.logl.jul.JULLoggerProviderTest", "bar", "foo.java", 32);
    StackTraceElement[] stackTrace = new StackTraceElement[] {element1, element2};
    cause.setStackTrace(stackTrace);

    logger.info("Logging logl->jul->buffer", cause);
    logger.debug("Logging logl->jul->buffer", cause);
    logger.warn("Logging logl->jul->buffer", cause);
    logger.error("Logging logl->jul->buffer", cause);

    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "INFO    [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.jul.JULLoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.jul.JULLoggerProviderTest.bar(foo.java:32)%n" +
        "WARNING [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.jul.JULLoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.jul.JULLoggerProviderTest.bar(foo.java:32)%n" +
        "SEVERE  [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.jul.JULLoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.jul.JULLoggerProviderTest.bar(foo.java:32)%n"));
    // @formatter:on
  }

  @Test
  void shouldLogSuppliedMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error(() -> "Logging logl->jul->buffer");
    logger.warn(() -> "Logging logl->jul->buffer");
    logger.debug(() -> "Logging logl->jul->buffer");
    logger.info(() -> "Logging logl->jul->buffer");
    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "SEVERE  [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n" +
        "WARNING [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n" +
        "INFO    [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n"));
    // @formatter:on
  }

  @Test
  void shouldLogSuppliedMessageAndCause() {
    Logger logger = loggerProvider.getLogger(getClass());
    RuntimeException cause = new RuntimeException("Something happened");
    StackTraceElement element1 = new StackTraceElement("org.logl.jul.JULLoggerProviderTest", "foo", "foo.java", 63);
    StackTraceElement element2 = new StackTraceElement("org.logl.jul.JULLoggerProviderTest", "bar", "foo.java", 32);
    StackTraceElement[] stackTrace = new StackTraceElement[] {element1, element2};
    cause.setStackTrace(stackTrace);

    logger.info(() -> "Logging logl->jul->buffer", cause);
    logger.debug(() -> "Logging logl->jul->buffer", cause);
    logger.warn(() -> "Logging logl->jul->buffer", cause);
    logger.error(() -> "Logging logl->jul->buffer", cause);

    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "INFO    [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.jul.JULLoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.jul.JULLoggerProviderTest.bar(foo.java:32)%n" +
        "WARNING [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.jul.JULLoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.jul.JULLoggerProviderTest.bar(foo.java:32)%n" +
        "SEVERE  [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.jul.JULLoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.jul.JULLoggerProviderTest.bar(foo.java:32)%n"));
    // @formatter:on
  }

  @Test
  void shouldLogFormattedErrorMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error("Logging {}->{}->{}", "logl", "jul", "buffer");
    logger.warnf("Logging %s->%s->%s", "logl", "jul", "buffer");
    logger.info("Logging {}->{}->{}", "logl", "jul", "buffer");
    logger.debugf("Logging %s->%s->%s", "logl", "jul", "buffer");
    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "SEVERE  [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n" +
        "WARNING [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n" +
        "INFO    [org.logl.jul.JULLoggerProviderTest] Logging logl->jul->buffer%n"));
    // @formatter:on
  }

  private String getBuffer() {
    return handler.getBuffer().toString();
  }
}

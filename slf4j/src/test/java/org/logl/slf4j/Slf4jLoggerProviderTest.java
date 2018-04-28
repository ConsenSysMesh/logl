package org.logl.slf4j;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logl.Logger;

class Slf4jLoggerProviderTest {

  private Slf4jLoggerProvider loggerProvider = new Slf4jLoggerProvider();

  @BeforeEach
  void setup() {
    getAppender().clear();
  }

  @Test
  void shouldLogMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error("Logging logl->slf4j->log4j2->buffer");
    logger.warn("Logging logl->slf4j->log4j2->buffer");
    logger.debug("Logging logl->slf4j->log4j2->buffer");
    logger.info("Logging logl->slf4j->log4j2->buffer");
    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "ERROR [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n" +
        "WARN  [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n" +
        "INFO  [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
    // @formatter:on
  }

  @Test
  void shouldLogMessageAndCause() {
    Logger logger = loggerProvider.getLogger(getClass());
    RuntimeException cause = new RuntimeException("Something happened");
    StackTraceElement element1 = new StackTraceElement("org.logl.Slf4jLoggerProviderTest", "foo", "foo.java", 63);
    StackTraceElement element2 = new StackTraceElement("org.logl.Slf4jLoggerProviderTest", "bar", "foo.java", 32);
    StackTraceElement[] stackTrace = new StackTraceElement[] {element1, element2};
    cause.setStackTrace(stackTrace);

    logger.info("Logging logl->slf4j->log4j2->buffer", cause);
    logger.debug("Logging logl->slf4j->log4j2->buffer", cause);
    logger.warn("Logging logl->slf4j->log4j2->buffer", cause);
    logger.error("Logging logl->slf4j->log4j2->buffer", cause);

    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "INFO  [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.Slf4jLoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.Slf4jLoggerProviderTest.bar(foo.java:32)%n" +
        "WARN  [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.Slf4jLoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.Slf4jLoggerProviderTest.bar(foo.java:32)%n" +
        "ERROR [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.Slf4jLoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.Slf4jLoggerProviderTest.bar(foo.java:32)%n"));
    // @formatter:on
  }

  @Test
  void shouldLogSuppliedMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error(() -> "Logging logl->slf4j->log4j2->buffer");
    logger.warn(() -> "Logging logl->slf4j->log4j2->buffer");
    logger.info(() -> "Logging logl->slf4j->log4j2->buffer");
    logger.debug(() -> "Logging logl->slf4j->log4j2->buffer");
    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "ERROR [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n" +
        "WARN  [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n" +
        "INFO  [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
    // @formatter:on
  }

  @Test
  void shouldLogSuppliedMessageAndCause() {
    Logger logger = loggerProvider.getLogger(getClass());
    RuntimeException cause = new RuntimeException("Something happened");
    StackTraceElement element1 = new StackTraceElement("org.logl.Slf4jLoggerProviderTest", "foo", "foo.java", 63);
    StackTraceElement element2 = new StackTraceElement("org.logl.Slf4jLoggerProviderTest", "bar", "foo.java", 32);
    StackTraceElement[] stackTrace = new StackTraceElement[] {element1, element2};
    cause.setStackTrace(stackTrace);

    logger.info(() -> "Logging logl->slf4j->log4j2->buffer", cause);
    logger.debug(() -> "Logging logl->slf4j->log4j2->buffer", cause);
    logger.warn(() -> "Logging logl->slf4j->log4j2->buffer", cause);
    logger.error(() -> "Logging logl->slf4j->log4j2->buffer", cause);

    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "INFO  [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.Slf4jLoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.Slf4jLoggerProviderTest.bar(foo.java:32)%n" +
        "WARN  [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.Slf4jLoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.Slf4jLoggerProviderTest.bar(foo.java:32)%n" +
        "ERROR [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.Slf4jLoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.Slf4jLoggerProviderTest.bar(foo.java:32)%n"));
    // @formatter:on
  }

  @Test
  void shouldLogFormattedErrorMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.errorf("Logging %s->%s->%s->%s", "logl", "slf4j", "log4j2", "buffer");
    logger.warn("Logging {}->{}->{}->{}", "logl", "slf4j", "log4j2", "buffer");
    logger.infof("Logging %s->%s->%s->%s", "logl", "slf4j", "log4j2", "buffer");
    logger.debug("ogging {}->{}->{}->{}", "logl", "slf4j", "log4j2", "buffer");
    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "ERROR [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n" +
        "WARN  [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n" +
        "INFO  [o.l.s.Slf4jLoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
    // @formatter:on
  }

  private static String getBuffer() {
    BufferingAppender bufferingAppender = getAppender();
    return bufferingAppender.buffer().toString();
  }

  private static BufferingAppender getAppender() {
    LoggerContext lc = (LoggerContext) LogManager.getContext(false);
    return (BufferingAppender) lc.getRootLogger().getAppenders().get("Buffer");
  }
}

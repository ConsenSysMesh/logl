package org.logl.log4j2;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logl.Level;
import org.logl.LogMessage;
import org.logl.Logger;

class Log4j2LoggerProviderTest {

  private Log4j2LoggerProvider loggerProvider = new Log4j2LoggerProvider();

  @BeforeEach
  void setup() {
    getAppender().clear();
  }

  @Test
  void shouldLogErrorWithPattern() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.errorf("Logging logl->slf4j->log4j2->%s", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("ERROR [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogErrorWithLogMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error(LogMessage.stringFormat("Logging logl->slf4j->log4j2->%s", "buffer"));
    assertThat(getBuffer())
        .isEqualTo(String.format("ERROR [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogErrorWithSubstitution() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error("Logging logl->slf4j->log4j2->{}", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("ERROR [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogErrorWithPatternWithTwoArgs() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.errorf("Logging logl->slf4j->%s->%s", "log4j2", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("ERROR [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogErrorWithPatternWithThreeArgs() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.errorf("Logging logl->%s->%s->%s", "slf4j", "log4j2", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("ERROR [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogInfoWithPattern() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.infof("Logging logl->slf4j->log4j2->%s", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("INFO  [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogInfoWithLogMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.info(LogMessage.stringFormat("Logging logl->slf4j->log4j2->%s", "buffer"));
    assertThat(getBuffer())
        .isEqualTo(String.format("INFO  [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogInfoWithSubstitution() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.info("Logging logl->slf4j->log4j2->{}", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("INFO  [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogInfoWithPatternWithTwoArgs() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.infof("Logging logl->slf4j->%s->%s", "log4j2", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("INFO  [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogInfoWithPatternWithThreeArgs() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.infof("Logging logl->%s->%s->%s", "slf4j", "log4j2", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("INFO  [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogWarnWithPattern() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.warnf("Logging logl->slf4j->log4j2->%s", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("WARN  [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogWarnWithLogMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.warn(LogMessage.stringFormat("Logging logl->slf4j->log4j2->%s", "buffer"));
    assertThat(getBuffer())
        .isEqualTo(String.format("WARN  [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogWarnWithSubstitution() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.warn("Logging logl->slf4j->log4j2->{}", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("WARN  [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogWarnWithPatternWithTwoArgs() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.warnf("Logging logl->slf4j->%s->%s", "log4j2", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("WARN  [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogWarnWithPatternWithThreeArgs() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.warnf("Logging logl->%s->%s->%s", "slf4j", "log4j2", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("WARN  [o.l.l.Log4j2LoggerProviderTest] Logging logl->slf4j->log4j2->buffer%n"));
  }

  @Test
  void shouldLogMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error("Logging logl->log4j2->buffer");
    logger.warn("Logging logl->log4j2->buffer");
    logger.debug("Logging logl->log4j2->buffer");
    logger.info("Logging logl->log4j2->buffer");
    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "ERROR [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "WARN  [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "INFO  [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n"));
    // @formatter:on
  }

  @Test
  void shouldLogMessageLog() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error(LogMessage.stringFormat("Logging logl->log4j2->%s", "buffer"));
    logger.warn(LogMessage.stringFormat("Logging logl->log4j2->%s", "buffer"));
    logger.info(LogMessage.stringFormat("Logging logl->log4j2->%s", "buffer"));
    logger.debug(LogMessage.stringFormat("Logging logl->log4j2->%s", "buffer"));
    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
      "ERROR [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "WARN  [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "INFO  [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n"));
    // @formatter:on
  }

  @Test
  void shouldLogMessageAndCause() {
    Logger logger = loggerProvider.getLogger(getClass());
    RuntimeException cause = new RuntimeException("Something happened");
    StackTraceElement element1 = new StackTraceElement("org.logl.Log4j2LoggerProviderTest", "foo", "foo.java", 63);
    StackTraceElement element2 = new StackTraceElement("org.logl.Log4j2LoggerProviderTest", "bar", "foo.java", 32);
    StackTraceElement[] stackTrace = new StackTraceElement[] {element1, element2};
    cause.setStackTrace(stackTrace);

    logger.info("Logging logl->log4j2->buffer", cause);
    logger.debug("Logging logl->log4j2->buffer", cause);
    logger.warn("Logging logl->log4j2->buffer", cause);
    logger.error("Logging logl->log4j2->buffer", cause);

    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "INFO  [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.Log4j2LoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.Log4j2LoggerProviderTest.bar(foo.java:32)%n" +
        "WARN  [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.Log4j2LoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.Log4j2LoggerProviderTest.bar(foo.java:32)%n" +
        "ERROR [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.Log4j2LoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.Log4j2LoggerProviderTest.bar(foo.java:32)%n"));
    // @formatter:on
  }

  @Test
  void shouldLogSuppliedMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error(() -> "Logging logl->log4j2->buffer");
    logger.warn(() -> "Logging logl->log4j2->buffer");
    logger.debug(() -> "Logging logl->log4j2->buffer");
    logger.info(() -> "Logging logl->log4j2->buffer");
    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "ERROR [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "WARN  [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "INFO  [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n"));
    // @formatter:on
  }

  @Test
  void shouldLogSuppliedMessageAndCause() {
    Logger logger = loggerProvider.getLogger(getClass());
    RuntimeException cause = new RuntimeException("Something happened");
    StackTraceElement element1 = new StackTraceElement("org.logl.Log4j2LoggerProviderTest", "foo", "foo.java", 63);
    StackTraceElement element2 = new StackTraceElement("org.logl.Log4j2LoggerProviderTest", "bar", "foo.java", 32);
    StackTraceElement[] stackTrace = new StackTraceElement[] {element1, element2};
    cause.setStackTrace(stackTrace);

    logger.info(() -> "Logging logl->log4j2->buffer", cause);
    logger.debug(() -> "Logging logl->log4j2->buffer", cause);
    logger.warn(() -> "Logging logl->log4j2->buffer", cause);
    logger.error(() -> "Logging logl->log4j2->buffer", cause);

    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "INFO  [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.Log4j2LoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.Log4j2LoggerProviderTest.bar(foo.java:32)%n" +
        "WARN  [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.Log4j2LoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.Log4j2LoggerProviderTest.bar(foo.java:32)%n" +
        "ERROR [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.Log4j2LoggerProviderTest.foo(foo.java:63)%n" +
        "\tat org.logl.Log4j2LoggerProviderTest.bar(foo.java:32)%n"));
    // @formatter:on
  }

  @Test
  void shouldLogFormattedErrorMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error("Logging {}->{}->{}", "logl", "log4j2", "buffer");
    logger.warnf("Logging %s->%s->%s", "logl", "log4j2", "buffer");
    logger.info("Logging {}->{}->{}", "logl", "log4j2", "buffer");
    logger.debugf("Logging %s->%s->%s", "logl", "log4j2", "buffer");
    // @formatter:off
    assertThat(getBuffer()).isEqualTo(String.format(
        "ERROR [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "WARN  [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n" +
        "INFO  [o.l.l.Log4j2LoggerProviderTest] Logging logl->log4j2->buffer%n"));
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

  @Test
  void levelShouldBeDebug() {
    Logger logger = loggerProvider.getLogger(getClass());
    assertEquals(Level.INFO, logger.getLevel());
  }

  @Test
  void levelEnabled() {
    Logger logger = loggerProvider.getLogger(getClass());
    assertFalse(logger.isEnabled(Level.DEBUG));
    assertTrue(logger.isEnabled(Level.INFO));
    assertTrue(logger.isEnabled(Level.WARN));
    assertTrue(logger.isEnabled(Level.ERROR));
  }
}

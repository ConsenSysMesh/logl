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
package org.logl.jul;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.logging.LogManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logl.LogMessage;
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
  void shouldLogErrorWithPattern() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.errorf("Logging logl->slf4j->jul->%s", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("SEVERE  [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogErrorWithLogMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error(LogMessage.stringFormat("Logging logl->slf4j->jul->%s", "buffer"));
    assertThat(getBuffer())
        .isEqualTo(String.format("SEVERE  [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogErrorWithSubstitution() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.error("Logging logl->slf4j->jul->{}", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("SEVERE  [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogErrorWithPatternWithTwoArgs() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.errorf("Logging logl->slf4j->%s->%s", "jul", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("SEVERE  [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogErrorWithPatternWithThreeArgs() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.errorf("Logging logl->%s->%s->%s", "slf4j", "jul", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("SEVERE  [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogInfoWithPattern() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.infof("Logging logl->slf4j->jul->%s", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("INFO    [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogInfoWithLogMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.info(LogMessage.stringFormat("Logging logl->slf4j->jul->%s", "buffer"));
    assertThat(getBuffer())
        .isEqualTo(String.format("INFO    [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogInfoWithSubstitution() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.info("Logging logl->slf4j->jul->{}", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("INFO    [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogInfoWithPatternWithTwoArgs() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.infof("Logging logl->slf4j->%s->%s", "jul", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("INFO    [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogInfoWithPatternWithThreeArgs() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.infof("Logging logl->%s->%s->%s", "slf4j", "jul", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("INFO    [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogWarnWithPattern() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.warnf("Logging logl->slf4j->jul->%s", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("WARNING [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogWarnWithLogMessage() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.warn(LogMessage.stringFormat("Logging logl->slf4j->jul->%s", "buffer"));
    assertThat(getBuffer())
        .isEqualTo(String.format("WARNING [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogWarnWithSubstitution() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.warn("Logging logl->slf4j->jul->{}", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("WARNING [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogWarnWithPatternWithTwoArgs() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.warnf("Logging logl->slf4j->%s->%s", "jul", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("WARNING [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
  }

  @Test
  void shouldLogWarnWithPatternWithThreeArgs() {
    Logger logger = loggerProvider.getLogger(getClass());
    logger.warnf("Logging logl->%s->%s->%s", "slf4j", "jul", "buffer");
    assertThat(getBuffer())
        .isEqualTo(String.format("WARNING [org.logl.jul.JULLoggerProviderTest] Logging logl->slf4j->jul->buffer%n"));
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

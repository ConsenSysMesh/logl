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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logl.Level;
import org.logl.LogMessage;
import org.logl.Logger;
import org.logl.LoggerProvider;

class UnformattedLoggerTest {

  private CharArrayWriter buffer;
  private PrintWriter out;

  @BeforeEach
  void setup() {
    buffer = new CharArrayWriter();
    out = new PrintWriter(buffer);
  }

  @Test
  void shouldOutputMessages() {
    LoggerProvider logProvider = UnformattedLogger.toPrintWriter(out);
    Logger logger = logProvider.getLogger("");

    logger.info("1output");
    logger.warn("2output");
    logger.debug("3output");
    // @formatter:off
    assertThat(buffer.toString()).isEqualTo(String.format(
        "1output%n" +
        "2output%n"));
    // @formatter:on
  }

  @Test
  void shouldOutputFormattedMessages() {
    LoggerProvider logProvider = UnformattedLogger.toPrintWriter(out);
    Logger logger = logProvider.getLogger("");

    logger.errorf("%d%s", 1, "output");
    logger.warn(LogMessage.messageFormat("{0}{1}", 2, "output"));
    logger.debug("3output");
    logger.info(LogMessage.stringFormat("4-%,d", 123456789));
    logger.info(LogMessage.patternFormat("{}{}", 5, "output"));
    logger.info("{}{}", 6, "output");
    // @formatter:off
    assertThat(buffer.toString()).isEqualTo(String.format(
        "1output%n" +
        "2output%n" +
        "4-123,456,789%n" +
        "5output%n" +
        "6output%n"));
    // @formatter:on
  }

  @Test
  void shouldOutputLocaleFormattedMessages() {
    LoggerProvider logProvider = UnformattedLogger.withLocale(Locale.GERMAN).toPrintWriter(out);
    Logger logger = logProvider.getLogger("");

    logger.errorf("%d%s", 1, "output");
    logger.warn(LogMessage.messageFormat("{0}{1}", 2, "output"));
    logger.debug("3output");
    logger.info(LogMessage.stringFormat("4-%,d", 123456789));
    logger.info(LogMessage.patternFormat("{}{}", 5, "output"));
    logger.info("{}{}", 6, "output");
    // @formatter:off
    assertThat(buffer.toString()).isEqualTo(String.format(
        "1output%n" +
        "2output%n" +
        "4-123.456.789%n" +
        "5output%n" +
        "6output%n"));
    // @formatter:on
  }

  @Test
  void shouldOutputExceptions() {
    LoggerProvider logProvider = UnformattedLogger.withLogLevel(Level.DEBUG).toPrintWriter(out);
    Logger logger = logProvider.getLogger("");

    RuntimeException exCause = new RuntimeException("Something happened");
    StackTraceElement causeElement1 =
        new StackTraceElement("org.logl.logl.UnformattedLoggerTest", "fail", "foo.java", 12);
    StackTraceElement causeElement2 =
        new StackTraceElement("org.logl.logl.UnformattedLoggerTest", "test", "foo.java", 14);
    StackTraceElement[] causeTrace = new StackTraceElement[] {causeElement1, causeElement2};
    exCause.setStackTrace(causeTrace);

    RuntimeException ex = new RuntimeException("Something happened", exCause);
    StackTraceElement element1 = new StackTraceElement("org.logl.logl.UnformattedLoggerTest", "foo", "foo.java", 63);
    StackTraceElement element2 = new StackTraceElement("org.logl.logl.UnformattedLoggerTest", "bar", "foo.java", 32);
    StackTraceElement[] stackTrace = new StackTraceElement[] {element1, element2};
    ex.setStackTrace(stackTrace);

    logger.errorf("%d%s", 1, "output");
    logger.info("{}{}", 2, "output");
    logger.debug("3output", ex);
    // @formatter:off
    assertThat(buffer.toString()).isEqualTo(String.format(
        "1output%n" +
        "2output%n" +
        "3output%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.logl.UnformattedLoggerTest.foo(foo.java:63)%n" +
        "\tat org.logl.logl.UnformattedLoggerTest.bar(foo.java:32)%n" +
        "Caused by: java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.logl.UnformattedLoggerTest.fail(foo.java:12)%n" +
        "\tat org.logl.logl.UnformattedLoggerTest.test(foo.java:14)%n"));
    // @formatter:on
  }

  @Test
  void shouldOutputBatchedMessages() {
    AdjustableLoggerProvider logProvider = UnformattedLogger.toPrintWriter(out);
    AdjustableLogger logger = logProvider.getLogger("");

    RuntimeException exCause = new RuntimeException("Something happened");
    StackTraceElement causeElement1 =
        new StackTraceElement("org.logl.logl.UnformattedLoggerTest", "fail", "foo.java", 12);
    StackTraceElement causeElement2 =
        new StackTraceElement("org.logl.logl.UnformattedLoggerTest", "test", "foo.java", 14);
    StackTraceElement[] causeTrace = new StackTraceElement[] {causeElement1, causeElement2};
    exCause.setStackTrace(causeTrace);

    RuntimeException ex = new RuntimeException("Something happened", exCause);
    StackTraceElement element1 = new StackTraceElement("org.logl.logl.UnformattedLoggerTest", "foo", "foo.java", 63);
    StackTraceElement element2 = new StackTraceElement("org.logl.logl.UnformattedLoggerTest", "bar", "foo.java", 32);
    StackTraceElement[] stackTrace = new StackTraceElement[] {element1, element2};
    ex.setStackTrace(stackTrace);

    logger.setLevel(Level.WARN);

    logger.errorf("%d%s", 1, "output");
    logger.info("{}{}", 2, "output");
    logger.batch(batchLogger -> {
      batchLogger.info("b1output");
      logger.info("3output");
      batchLogger.warn("b2{}", "output");
      logger.setLevel(Level.INFO);
    });
    logger.info("4output", ex);
    // @formatter:off
    assertThat(buffer.toString()).isEqualTo(String.format(
        "1output%n" +
        "b1output%n" +
        "b2output%n" +
        "4output%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.logl.UnformattedLoggerTest.foo(foo.java:63)%n" +
        "\tat org.logl.logl.UnformattedLoggerTest.bar(foo.java:32)%n" +
        "Caused by: java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.logl.UnformattedLoggerTest.fail(foo.java:12)%n" +
        "\tat org.logl.logl.UnformattedLoggerTest.test(foo.java:14)%n"));
    // @formatter:on
  }

  @Test
  void shouldNotOutputLogsWhenSetToLevelNone() {
    AdjustableLoggerProvider logProvider = UnformattedLogger.toPrintWriter(out);
    AdjustableLogger logger = logProvider.getLogger("");

    logger.setLevel(Level.WARN);

    assertThat(logger.isEnabled(Level.ERROR)).isTrue();

    logger.info("{}{}", 1, "output");
    logger.warn("{}{}", 2, "output");

    logger.setLevel(Level.NONE);
    logger.error("{}{}", 3, "output");

    assertThat(logger.isEnabled(Level.ERROR)).isFalse();
    assertThat(logger.isEnabled(Level.NONE)).isFalse();

    // @formatter:off
    assertThat(buffer.toString()).isEqualTo(String.format(
        "2output%n"));
    // @formatter:on
  }

  @Test
  void shouldLogConcurrently() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(
        new BufferedWriter(new OutputStreamWriter(new PrintStream(outputStream), Charset.defaultCharset())));
    AdjustableLoggerProvider provider = UnformattedLogger.toPrintWriter(printWriter);

    Logger logger1 = provider.getLogger("a.b_first.logger");
    Logger logger2 = provider.getLogger("a.b_second.logger");

    Callable<Void> logToFirst = () -> {
      logger1.warn("A {} log message", "simple");
      return null;
    };
    Callable<Void> logToSecond = () -> {
      logger2.warn("A {} log message", "simple");
      return null;
    };

    List<Callable<Void>> callables = new ArrayList<>(Collections.nCopies(100, logToFirst));
    callables.addAll(Collections.nCopies(100, logToSecond));
    Collections.shuffle(callables);

    ExecutorService pool = Executors.newFixedThreadPool(20);
    List<Future<Void>> futures = pool.invokeAll(callables);
    for (Future<Void> future : futures) {
      future.get();
    }

    String output = new String(outputStream.toByteArray(), UTF_8);
    String lines[] = output.split(System.lineSeparator(), 0);
    assertThat(lines.length).isEqualTo(200);
    for (String line : lines) {
      assertThat(line).isEqualTo("A simple log message");
    }
  }
}

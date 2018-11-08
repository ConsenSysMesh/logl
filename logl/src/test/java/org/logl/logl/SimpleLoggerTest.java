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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.time.Instant;
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

class SimpleLoggerTest {

  private CharArrayWriter buffer;
  private PrintWriter out;
  private Instant now = Instant.parse("2007-12-03T10:15:30.00Z");

  @BeforeEach
  void setup() {
    buffer = new CharArrayWriter();
    out = new PrintWriter(buffer);
  }

  @Test
  void shouldOutputMessages() {
    LoggerProvider logProvider = SimpleLogger.usingCurrentTimeSupplier(() -> now).toPrintWriter(out);
    Logger logger = logProvider.getLogger("org.logl.StandardLoggerTest");

    logger.info("1output");
    now = now.plusMillis(700);
    logger.warn("2output");
    logger.debug("3output");
    // @formatter:off
    assertThat(buffer.toString()).isEqualTo(String.format(
        "2007-12-03 10:15:30.000+0000  INFO [o.l.StandardLoggerTest] 1output%n" +
        "2007-12-03 10:15:30.700+0000  WARN [o.l.StandardLoggerTest] 2output%n"));
    // @formatter:on
  }

  @Test
  void shouldOutputFormattedMessages() {
    LoggerProvider logProvider = SimpleLogger.usingCurrentTimeSupplier(() -> now).toPrintWriter(out);
    Logger logger = logProvider.getLogger(getClass());

    logger.errorf("%d%s", 1, "output");
    now = now.plusMillis(1400);
    logger.warn(LogMessage.messageFormat("{0}{1}", 2, "output"));
    now = now.plusMillis(1400);
    logger.debug("3output");
    now = now.plusMillis(1400);
    logger.info(LogMessage.stringFormat("4-%,d", 123456789));
    logger.info(LogMessage.patternFormat("{}{}", 5, "output"));
    logger.info("{}{}", 6, "output");
    // @formatter:off
    assertThat(buffer.toString()).isEqualTo(String.format(
        "2007-12-03 10:15:30.000+0000 ERROR [o.l.l.SimpleLoggerTest] 1output%n" +
        "2007-12-03 10:15:31.400+0000  WARN [o.l.l.SimpleLoggerTest] 2output%n" +
        "2007-12-03 10:15:34.200+0000  INFO [o.l.l.SimpleLoggerTest] 4-123,456,789%n" +
        "2007-12-03 10:15:34.200+0000  INFO [o.l.l.SimpleLoggerTest] 5output%n" +
        "2007-12-03 10:15:34.200+0000  INFO [o.l.l.SimpleLoggerTest] 6output%n"));
    // @formatter:on
  }

  @Test
  void shouldOutputLocaleFormattedMessages() {
    LoggerProvider logProvider =
        SimpleLogger.usingCurrentTimeSupplier(() -> now).withLocale(Locale.GERMAN).toPrintWriter(out);
    Logger logger = logProvider.getLogger("");

    logger.errorf("%d%s", 1, "output");
    now = now.plusMillis(900);
    logger.warn(LogMessage.messageFormat("{0}{1}", 2, "output"));
    now = now.plusMillis(900);
    logger.debug("3output");
    now = now.plusMillis(900);
    logger.info(LogMessage.stringFormat("4-%,d", 123456789));
    logger.info(LogMessage.patternFormat("{}{}", 5, "output"));
    logger.info("{}{}", 6, "output");
    // @formatter:off
    assertThat(buffer.toString()).isEqualTo(String.format(
        "2007-12-03 10:15:30.000+0000 ERROR [] 1output%n" +
        "2007-12-03 10:15:30.900+0000  WARN [] 2output%n" +
        "2007-12-03 10:15:32.700+0000  INFO [] 4-123.456.789%n" +
        "2007-12-03 10:15:32.700+0000  INFO [] 5output%n" +
        "2007-12-03 10:15:32.700+0000  INFO [] 6output%n"));
    // @formatter:on
  }

  @Test
  void shouldOutputExceptions() {
    LoggerProvider logProvider =
        SimpleLogger.usingCurrentTimeSupplier(() -> now).withLogLevel(Level.DEBUG).toPrintWriter(out);
    Logger logger = logProvider.getLogger(SimpleLogger.class);

    RuntimeException exCause = new RuntimeException("Something happened");
    StackTraceElement causeElement1 = new StackTraceElement("org.logl.logl.SimpleLoggerTest", "fail", "foo.java", 12);
    StackTraceElement causeElement2 = new StackTraceElement("org.logl.logl.SimpleLoggerTest", "test", "foo.java", 14);
    StackTraceElement[] causeTrace = new StackTraceElement[] {causeElement1, causeElement2};
    exCause.setStackTrace(causeTrace);

    RuntimeException ex = new RuntimeException("Something happened", exCause);
    StackTraceElement element1 = new StackTraceElement("org.logl.logl.SimpleLoggerTest", "foo", "foo.java", 63);
    StackTraceElement element2 = new StackTraceElement("org.logl.logl.SimpleLoggerTest", "bar", "foo.java", 32);
    StackTraceElement[] stackTrace = new StackTraceElement[] {element1, element2};
    ex.setStackTrace(stackTrace);

    logger.errorf("%d%s", 1, "output");
    logger.info("{}{}", 2, "output");
    logger.debug("3output", ex);
    // @formatter:off
    assertThat(buffer.toString()).isEqualTo(String.format(
        "2007-12-03 10:15:30.000+0000 ERROR [o.l.l.SimpleLogger] 1output%n" +
        "2007-12-03 10:15:30.000+0000  INFO [o.l.l.SimpleLogger] 2output%n" +
        "2007-12-03 10:15:30.000+0000 DEBUG [o.l.l.SimpleLogger] 3output%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.logl.SimpleLoggerTest.foo(foo.java:63)%n" +
        "\tat org.logl.logl.SimpleLoggerTest.bar(foo.java:32)%n" +
        "Caused by: java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.logl.SimpleLoggerTest.fail(foo.java:12)%n" +
        "\tat org.logl.logl.SimpleLoggerTest.test(foo.java:14)%n"));
    // @formatter:on
  }

  @Test
  void shouldOutputBatchedMessages() {
    AdjustableLoggerProvider logProvider = SimpleLogger.usingCurrentTimeSupplier(() -> now).toPrintWriter(out);
    AdjustableLogger logger = logProvider.getLogger(SimpleLogger.class);

    RuntimeException exCause = new RuntimeException("Something happened");
    StackTraceElement causeElement1 = new StackTraceElement("org.logl.logl.SimpleLoggerTest", "fail", "foo.java", 12);
    StackTraceElement causeElement2 = new StackTraceElement("org.logl.logl.SimpleLoggerTest", "test", "foo.java", 14);
    StackTraceElement[] causeTrace = new StackTraceElement[] {causeElement1, causeElement2};
    exCause.setStackTrace(causeTrace);

    RuntimeException ex = new RuntimeException("Something happened", exCause);
    StackTraceElement element1 = new StackTraceElement("org.logl.logl.SimpleLoggerTest", "foo", "foo.java", 63);
    StackTraceElement element2 = new StackTraceElement("org.logl.logl.SimpleLoggerTest", "bar", "foo.java", 32);
    StackTraceElement[] stackTrace = new StackTraceElement[] {element1, element2};
    ex.setStackTrace(stackTrace);

    logger.setLevel(Level.WARN);

    logger.errorf("%d%s", 1, "output");
    now = now.plusMillis(900);
    logger.info("{}{}", 2, "output");
    now = now.plusMillis(900);
    logger.batch(batchLogger -> {
      batchLogger.info("b1output");
      now = now.plusMillis(900);
      logger.info("3output");
      now = now.plusMillis(900);
      batchLogger.warn("b2{}", "output");
      now = now.plusMillis(900);
      logger.setLevel(Level.INFO);
    });
    logger.info("4output", ex);
    // @formatter:off
    assertThat(buffer.toString()).isEqualTo(String.format(
        "2007-12-03 10:15:30.000+0000 ERROR [o.l.l.SimpleLogger] 1output%n" +
        "2007-12-03 10:15:31.800+0000  INFO [o.l.l.SimpleLogger] b1output%n" +
        "2007-12-03 10:15:33.600+0000  WARN [o.l.l.SimpleLogger] b2output%n" +
        "2007-12-03 10:15:34.500+0000  INFO [o.l.l.SimpleLogger] 4output%n" +
        "java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.logl.SimpleLoggerTest.foo(foo.java:63)%n" +
        "\tat org.logl.logl.SimpleLoggerTest.bar(foo.java:32)%n" +
        "Caused by: java.lang.RuntimeException: Something happened%n" +
        "\tat org.logl.logl.SimpleLoggerTest.fail(foo.java:12)%n" +
        "\tat org.logl.logl.SimpleLoggerTest.test(foo.java:14)%n"));
    // @formatter:on
  }

  @Test
  void shouldNotOutputLogsWhenSetToLevelNone() {
    AdjustableLoggerProvider logProvider = SimpleLogger.usingCurrentTimeSupplier(() -> now).toPrintWriter(out);
    AdjustableLogger logger = logProvider.getLogger(SimpleLogger.class);

    logger.setLevel(Level.WARN);

    assertThat(logger.isEnabled(Level.ERROR)).isTrue();

    logger.info("{}{}", 1, "output");
    now = now.plusMillis(900);
    logger.warn("{}{}", 2, "output");
    now = now.plusMillis(900);

    logger.setLevel(Level.NONE);
    logger.error("{}{}", 3, "output");

    assertThat(logger.isEnabled(Level.ERROR)).isFalse();
    assertThat(logger.isEnabled(Level.NONE)).isFalse();

    // @formatter:off
    assertThat(buffer.toString()).isEqualTo(String.format(
        "2007-12-03 10:15:30.900+0000  WARN [o.l.l.SimpleLogger] 2output%n"));
    // @formatter:on
  }

  @Test
  void shouldFlushByDefaultWithPrintWriter() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(
        new BufferedWriter(new OutputStreamWriter(new PrintStream(outputStream), Charset.defaultCharset())));
    Logger logger =
        SimpleLogger.withLogLevel(Level.DEBUG).usingCurrentTimeSupplier(() -> now).toPrintWriter(printWriter).getLogger(
            "foo");
    logger.debug("bar");
    String logged = new String(outputStream.toByteArray(), UTF_8);
    assertTrue(logged.endsWith(String.format(" DEBUG [foo] bar%n")), logged);
  }

  @Test
  void shouldFlushByDefaultWithOutputStream() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Logger logger = SimpleLogger
        .withLogLevel(Level.DEBUG)
        .usingCurrentTimeSupplier(() -> now)
        .toOutputStream(outputStream)
        .getLogger("foo");
    logger.debug("bar");
    String logged = new String(outputStream.toByteArray(), UTF_8);
    assertTrue(logged.endsWith(String.format(" DEBUG [foo] bar%n")), logged);
  }

  @Test
  void shouldFlushWhenLoggingExceptionByDefaultWithPrintWriter() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(
        new BufferedWriter(new OutputStreamWriter(new PrintStream(outputStream), Charset.defaultCharset())));
    Logger logger =
        SimpleLogger.withLogLevel(Level.DEBUG).usingCurrentTimeSupplier(() -> now).toPrintWriter(printWriter).getLogger(
            "foo");
    logger.debug("bar", new Exception());
    String logged = new String(outputStream.toByteArray(), UTF_8);
    assertTrue(logged.contains(" DEBUG [foo] bar"), logged);
  }

  @Test
  void shouldFlushWhenLoggingExceptionByDefaultWithOutputStream() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Logger logger = SimpleLogger
        .withLogLevel(Level.DEBUG)
        .usingCurrentTimeSupplier(() -> now)
        .toOutputStream(outputStream)
        .getLogger("foo");
    logger.debug("bar", new Exception());
    String logged = new String(outputStream.toByteArray(), UTF_8);
    assertTrue(logged.contains(" DEBUG [foo] bar"), logged);
  }

  @Test
  void shouldNotFlushIfAsked() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(
        new BufferedWriter(new OutputStreamWriter(new PrintStream(outputStream), Charset.defaultCharset())));
    Logger logger = SimpleLogger
        .withLogLevel(Level.DEBUG)
        .withoutAutoFlush()
        .usingCurrentTimeSupplier(() -> now)
        .toPrintWriter(printWriter)
        .getLogger("foo");
    logger.debug("bar", new Exception());
    String logged = new String(outputStream.toByteArray(), UTF_8);
    assertTrue(logged.isEmpty(), logged);
    printWriter.flush();
    logged = new String(outputStream.toByteArray(), UTF_8);
    assertTrue(logged.contains(" DEBUG [foo] bar"), logged);
  }

  @Test
  void shouldLogConcurrently() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(
        new BufferedWriter(new OutputStreamWriter(new PrintStream(outputStream), Charset.defaultCharset())));
    AdjustableLoggerProvider provider =
        SimpleLogger.withLogLevel(Level.DEBUG).usingCurrentTimeSupplier(() -> now).toPrintWriter(printWriter);

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
      assertThat(line).isEqualTo("2007-12-03 10:15:30.000+0000  WARN [a.b.logger] A simple log message");
    }
  }
}

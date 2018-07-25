package org.logl.vertx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.logl.Level;
import org.logl.LogMessage;
import org.logl.LogWriter;
import org.logl.Logger;
import org.logl.LoggerProvider;

class InteractionRecordingLoggerProviderTest {

  @Test
  void recordAndReplay() {
    InteractionRecordingLoggerProvider provider = new InteractionRecordingLoggerProvider();
    provider.getLogger("foo").debug("First message");
    provider.getLogger("bar").debug("Second message");
    provider.getLogger("foobar").debug("Third message");

    StringWriter writer = new StringWriter();
    LogWriter logWriter = new LogWriter() {
      @Override
      public void log(LogMessage message) {
        try {
          message.appendTo(Locale.getDefault(), writer);
          writer.append("\n");
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }

      @Override
      public void log(LogMessage message, Throwable cause) {

      }

      @Override
      public void batch(Consumer<LogWriter> fn) {

      }
    };

    provider.replayTo(new LoggerProvider() {
      @Override
      public Logger getLogger(String name) {
        return new Logger() {
          @Override
          public Level getLevel() {
            throw new UnsupportedOperationException();
          }

          @Override
          public LogWriter errorWriter() {
            return logWriter;
          }

          @Override
          public LogWriter warnWriter() {
            return logWriter;
          }

          @Override
          public LogWriter infoWriter() {
            return logWriter;
          }

          @Override
          public LogWriter debugWriter() {
            return logWriter;
          }

          @Override
          public void batch(Consumer<Logger> fn) {

          }
        };
      }
    });
    writer.flush();
    assertEquals("First message\nSecond message\nThird message\n", writer.toString());
  }

}

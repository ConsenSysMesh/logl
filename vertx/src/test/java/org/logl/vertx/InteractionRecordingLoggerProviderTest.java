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

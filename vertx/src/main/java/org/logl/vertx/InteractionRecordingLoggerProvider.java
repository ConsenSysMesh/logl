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

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

import org.logl.Level;
import org.logl.LogMessage;
import org.logl.LogWriter;
import org.logl.Logger;
import org.logl.LoggerProvider;

final class InteractionRecordingLoggerProvider implements LoggerProvider {
  private final Deque<Consumer<LoggerProvider>> record = new ConcurrentLinkedDeque<>();

  @Override
  public Logger getLogger(String name) {
    return new InteractionRecordingLogger(name);
  }

  void replayTo(LoggerProvider provider) {
    record.forEach(e -> e.accept(provider));
  }

  private final class InteractionRecordingLogger implements Logger {
    private final String name;
    private final LogWriter errorWriter;
    private final LogWriter warnWriter;
    private final LogWriter infoWriter;
    private final LogWriter debugWriter;

    InteractionRecordingLogger(String name) {
      this.name = name;
      this.errorWriter = new InteractionRecordingLogWriter(Level.ERROR);
      this.warnWriter = new InteractionRecordingLogWriter(Level.WARN);
      this.infoWriter = new InteractionRecordingLogWriter(Level.INFO);
      this.debugWriter = new InteractionRecordingLogWriter(Level.DEBUG);
    }

    @Override
    public Level getLevel() {
      return Level.DEBUG;
    }

    @Override
    public LogWriter errorWriter() {
      return errorWriter;
    }

    @Override
    public LogWriter warnWriter() {
      return warnWriter;
    }

    @Override
    public LogWriter infoWriter() {
      return infoWriter;
    }

    @Override
    public LogWriter debugWriter() {
      return debugWriter;
    }

    @Override
    public void batch(Consumer<Logger> fn) {
      // vertx doesn't use batch logging
      throw new UnsupportedOperationException();
    }

    private final class InteractionRecordingLogWriter implements LogWriter {
      private final Level level;

      InteractionRecordingLogWriter(Level level) {
        this.level = level;
      }

      @Override
      public void log(LogMessage message) {
        record.add(p -> p.getLogger(name).writer(level).log(message));
      }

      @Override
      public void log(LogMessage message, Throwable cause) {
        record.add(p -> p.getLogger(name).writer(level).log(message, cause));
      }

      @Override
      public void batch(Consumer<LogWriter> fn) {
        // vertx doesn't use batch logging
        throw new UnsupportedOperationException();
      }
    }
  }
}

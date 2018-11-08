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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.logl.Level;
import org.logl.LogMessage;
import org.logl.LogWriter;

final class DuplicatingLogWriter implements LogWriter {
  private final Level level;
  private final DuplicatingLogger logger;
  private final List<LogWriter> logWriters;

  DuplicatingLogWriter(Level level, DuplicatingLogger logger, List<LogWriter> logWriters) {
    this.level = level;
    this.logger = logger;
    this.logWriters = new CopyOnWriteArrayList<>(logWriters);
  }

  boolean remove(LogWriter logWriter) {
    return logWriters.remove(logWriter);
  }

  @Override
  public void log(LogMessage message) {
    if (logger.checkLevel(level)) {
      for (LogWriter logWriter : logWriters) {
        logWriter.log(message);
      }
    }
  }

  @Override
  public void log(CharSequence message) {
    if (logger.checkLevel(level)) {
      for (LogWriter logWriter : logWriters) {
        logWriter.log(message);
      }
    }
  }

  @Override
  public void log(Supplier<? extends CharSequence> messageSupplier) {
    if (logger.checkLevel(level)) {
      CharSequence message = messageSupplier.get();
      for (LogWriter logWriter : logWriters) {
        logWriter.log(message);
      }
    }
  }

  @Override
  public void log(LogMessage message, Throwable cause) {
    if (logger.checkLevel(level)) {
      for (LogWriter logWriter : logWriters) {
        logWriter.log(message, cause);
      }
    }
  }

  @Override
  public void log(CharSequence message, Throwable cause) {
    if (logger.checkLevel(level)) {
      for (LogWriter logWriter : logWriters) {
        logWriter.log(message, cause);
      }
    }
  }

  @Override
  public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    if (logger.checkLevel(level)) {
      CharSequence message = messageSupplier.get();
      for (LogWriter logWriter : logWriters) {
        logWriter.log(message, cause);
      }
    }
  }

  @Override
  public void log(String pattern, Object... args) {
    if (logger.checkLevel(level)) {
      for (LogWriter logWriter : logWriters) {
        logWriter.log(pattern, args);
      }
    }
  }

  @Override
  public void logf(String format, Object... args) {
    if (logger.checkLevel(level)) {
      for (LogWriter logWriter : logWriters) {
        logWriter.log(format, args);
      }
    }
  }

  @Override
  public void batch(Consumer<LogWriter> fn) {
    batch(0, new ArrayList<>(logWriters.size()), fn);
  }

  private void batch(int index, ArrayList<LogWriter> batchLoggers, Consumer<LogWriter> fn) {
    if (index < logWriters.size()) {
      LogWriter logWriter = logWriters.get(index);
      logWriter.batch(bulkLogger -> {
        batchLoggers.add(bulkLogger);
        batch(index + 1, batchLoggers, fn);
      });
    } else {
      fn.accept(new DuplicatingLogWriter(level, logger, batchLoggers));
    }
  }
}

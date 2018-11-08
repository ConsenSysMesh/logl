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


import java.util.function.Consumer;
import java.util.function.Supplier;

import org.logl.Level;
import org.logl.LogMessage;
import org.logl.LogWriter;

final class LevelLogWriter implements LogWriter {
  private final Level level;
  private final LevelLogger logger;

  LevelLogWriter(Level level, LevelLogger logger) {
    this.level = level;
    this.logger = logger;
  }

  @Override
  public void log(LogMessage message) {
    logger.log(level, message);
  }

  @Override
  public void log(CharSequence message) {
    logger.log(level, message);
  }

  @Override
  public void log(Supplier<? extends CharSequence> messageSupplier) {
    logger.log(level, messageSupplier);
  }

  @Override
  public void log(LogMessage message, Throwable cause) {
    logger.log(level, message, cause);
  }

  @Override
  public void log(CharSequence message, Throwable cause) {
    logger.log(level, message, cause);
  }

  @Override
  public void log(Supplier<? extends CharSequence> messageSupplier, Throwable cause) {
    logger.log(level, messageSupplier, cause);
  }

  @Override
  public void logf(String format, Object... args) {
    logger.logf(level, format, args);
  }

  @Override
  public void batch(Consumer<LogWriter> fn) {
    logger.batch(level, fn);
  }
}

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
package org.logl;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

final class NullLogger implements Logger {
  private static final NullLogger INSTANCE = new NullLogger();

  static NullLogger instance() {
    return INSTANCE;
  }

  private NullLogger() {}

  private LogWriter logWriter = NullLogWriter.instance();

  @Override
  public Level getLevel() {
    return Level.NONE;
  }

  @Override
  public boolean isEnabled(Level level) {
    requireNonNull(level);
    return false;
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
    requireNonNull(fn);
    fn.accept(this);
  }
}

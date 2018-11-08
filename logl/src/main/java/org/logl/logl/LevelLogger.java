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

interface LevelLogger {

  void log(Level level, LogMessage message);

  void log(Level level, CharSequence message);

  void log(Level level, Supplier<? extends CharSequence> messageSupplier);

  void log(Level level, LogMessage message, Throwable cause);

  void log(Level level, CharSequence message, Throwable cause);

  void log(Level level, Supplier<? extends CharSequence> messageSupplier, Throwable cause);

  default void log(Level level, String pattern, Object... args) {
    if (args.length == 1 && args[0] instanceof Throwable) {
      log(level, pattern, (Throwable) args[0]);
    } else {
      log(level, LogMessage.patternFormat(pattern, args));
    }
  }

  default void logf(Level level, String format, Object... args) {
    if (args.length == 1 && args[0] instanceof Throwable) {
      log(level, format, (Throwable) args[0]);
    } else {
      log(level, LogMessage.stringFormat(format, args));
    }
  }

  void batch(Level level, Consumer<LogWriter> fn);
}

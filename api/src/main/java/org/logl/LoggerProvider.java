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

/**
 * A provider for {@link Logger} instances.
 */
public interface LoggerProvider {

  /**
   * @return A {@link LoggerProvider} that returns {@link Logger} instances that discards all log messages.
   */
  static LoggerProvider nullProvider() {
    return NullLoggerProvider.instance();
  }

  /**
   * Obtain a logger for a given class (by name).
   *
   * @param loggingClass The class to obtain a {@link Logger} for.
   * @return A {@link Logger} instance for the class (by name).
   */
  default Logger getLogger(Class<?> loggingClass) {
    return getLogger(loggerName(loggingClass));
  }

  /**
   * Obtain a logger for a specified name.
   *
   * @param name The name to obtain a {@link Logger} for.
   * @return A {@link Logger} instance for the specified name.
   */
  Logger getLogger(String name);

  /**
   * Utility function for obtaining the logger name for a class.
   *
   * @param clazz The class to obtain a logger name for.
   * @return The name of the class.
   */
  static String loggerName(Class<?> clazz) {
    return clazz.isAnonymousClass() ? clazz.getEnclosingClass().getCanonicalName() : clazz.getCanonicalName();
  }
}

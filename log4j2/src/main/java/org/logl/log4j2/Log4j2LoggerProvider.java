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
package org.logl.log4j2;

import static org.logl.LoggerProvider.loggerName;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.logl.Logger;
import org.logl.LoggerProvider;

/**
 * An implementation of a {@link org.logl.LoggerProvider} that delegates to {@link org.apache.logging.log4j.LogManager
 * org.apache.logging.log4j.LogManager}.
 */
public class Log4j2LoggerProvider implements LoggerProvider {
  private Function<String, org.apache.logging.log4j.Logger> loggerFactory;

  /**
   * Create a logger provider that uses {@link LogManager#getLogger(String)} for obtaining a logger.
   */
  public Log4j2LoggerProvider() {
    this(LogManager::getLogger);
  }

  /**
   * @param loggerFactory A function for obtaining log4j2 loggers.
   */
  public Log4j2LoggerProvider(Function<String, org.apache.logging.log4j.Logger> loggerFactory) {
    this.loggerFactory = loggerFactory;
  }

  @Override
  public Logger getLogger(Class<?> loggingClass) {
    return new Log4j2Logger(loggerFactory.apply(loggerName(loggingClass)));
  }

  @Override
  public Logger getLogger(String name) {
    return new Log4j2Logger(loggerFactory.apply(name));
  }
}

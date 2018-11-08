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
package org.logl.slf4j;

import static org.logl.LoggerProvider.loggerName;

import java.util.function.Function;

import org.logl.Logger;
import org.logl.LoggerProvider;
import org.slf4j.LoggerFactory;

/**
 * An implementation of a {@link org.logl.LoggerProvider} that delegates to {@link org.slf4j.LoggerFactory
 * org.slf4j.LoggerFactory}.
 */
public class Slf4jLoggerProvider implements LoggerProvider {
  private Function<String, org.slf4j.Logger> loggerFactory;

  /**
   * Create a logger provider that uses {@link LoggerFactory#getLogger(String)} for obtaining a logger.
   */
  public Slf4jLoggerProvider() {
    this(LoggerFactory::getLogger);
  }

  /**
   * @param loggerFactory A function for obtaining slf4j loggers.
   */
  public Slf4jLoggerProvider(Function<String, org.slf4j.Logger> loggerFactory) {
    this.loggerFactory = loggerFactory;
  }

  @Override
  public Logger getLogger(Class<?> loggingClass) {
    return new Slf4jLogger(loggerFactory.apply(loggerName(loggingClass)));
  }

  @Override
  public Logger getLogger(String name) {
    return new Slf4jLogger(loggerFactory.apply(name));
  }
}

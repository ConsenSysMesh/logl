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

import io.vertx.core.spi.logging.LogDelegate;
import org.logl.LogMessage;
import org.logl.Logger;

final class LoglLogDelegate implements LogDelegate {

  private volatile Logger logger;

  LoglLogDelegate(Logger logger) {
    this.logger = logger;
  }

  void setLogger(Logger logger) {
    this.logger = logger;
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return false;
  }

  @Override
  public void fatal(Object message) {
    if (logger.isErrorEnabled()) {
      logger.error(message.toString());
    }
  }

  @Override
  public void fatal(Object message, Throwable t) {
    if (logger.isErrorEnabled()) {
      logger.error(message.toString(), t);
    }
  }

  @Override
  public void error(Object message) {
    if (logger.isErrorEnabled()) {
      logger.error(message.toString());
    }
  }

  @Override
  public void error(Object message, Object... params) {
    logger.error(message.toString(), params);
  }

  @Override
  public void error(Object message, Throwable t) {
    if (logger.isErrorEnabled()) {
      logger.error(message.toString(), t);
    }
  }

  @Override
  public void error(Object message, Throwable t, Object... params) {
    if (logger.isErrorEnabled()) {
      logger.error(LogMessage.patternFormat(message.toString(), params), t);
    }
  }

  @Override
  public void warn(Object message) {
    if (logger.isWarnEnabled()) {
      logger.warn(message.toString());
    }
  }

  @Override
  public void warn(Object message, Object... params) {
    logger.warn(message.toString(), params);
  }

  @Override
  public void warn(Object message, Throwable t) {
    if (logger.isWarnEnabled()) {
      logger.warn(message.toString(), t);
    }
  }

  @Override
  public void warn(Object message, Throwable t, Object... params) {
    if (logger.isWarnEnabled()) {
      logger.warn(LogMessage.patternFormat(message.toString(), params), t);
    }
  }

  @Override
  public void info(Object message) {
    if (logger.isInfoEnabled()) {
      logger.info(message.toString());
    }
  }

  @Override
  public void info(Object message, Object... params) {
    logger.info(message.toString(), params);
  }

  @Override
  public void info(Object message, Throwable t) {
    if (logger.isInfoEnabled()) {
      logger.info(message.toString(), t);
    }
  }

  @Override
  public void info(Object message, Throwable t, Object... params) {
    if (logger.isInfoEnabled()) {
      logger.info(LogMessage.patternFormat(message.toString(), params), t);
    }
  }

  @Override
  public void debug(Object message) {
    if (logger.isDebugEnabled()) {
      logger.debug(message.toString());
    }
  }

  @Override
  public void debug(Object message, Object... params) {
    logger.debug(message.toString(), params);
  }

  @Override
  public void debug(Object message, Throwable t) {
    if (logger.isDebugEnabled()) {
      logger.debug(message.toString(), t);
    }
  }

  @Override
  public void debug(Object message, Throwable t, Object... params) {
    if (logger.isDebugEnabled()) {
      logger.debug(LogMessage.patternFormat(message.toString(), params), t);
    }
  }

  @Override
  public void trace(Object message) {}

  @Override
  public void trace(Object message, Object... params) {}

  @Override
  public void trace(Object message, Throwable t) {}

  @Override
  public void trace(Object message, Throwable t, Object... params) {}
}

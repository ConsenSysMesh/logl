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

import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name = "BufferingAppender" , category = "Core" , elementType = "appender" , printObject = true)
public class BufferingAppender extends AbstractAppender {

  @PluginFactory
  public static BufferingAppender createAppender(@PluginAttribute("name") String name) {
    if (name == null) {
      LOGGER.error("No name provided for AccumulatingAppender");
      return null;
    }
    return new BufferingAppender(name, null);
  }

  @SuppressWarnings("JdkObsolete")
  private StringBuffer buffer = new StringBuffer();

  private BufferingAppender(String name, Filter filter) {
    super(name, filter, PatternLayout.newBuilder().withPattern("%-5p [%c{1.}] %m%n%ex").build(), true);
  }

  @Override
  public void append(LogEvent event) {
    final byte[] bytes = getLayout().toByteArray(event);
    buffer.append(new String(bytes, StandardCharsets.UTF_8));
  }

  StringBuffer buffer() {
    return buffer;
  }

  void clear() {
    buffer.setLength(0);
  }
}

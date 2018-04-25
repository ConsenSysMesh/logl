package org.logl.slf4j;

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

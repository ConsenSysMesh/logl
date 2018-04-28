package org.logl.jul;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

final class StringWriterHandler extends Handler {

  private StringWriter stringWriter = new StringWriter();
  private PrintWriter writer = new PrintWriter(stringWriter);

  @Override
  public void publish(LogRecord record) {
    writer.printf("%-7s [%s] %s%n", record.getLevel(), record.getLoggerName(), record.getMessage());
    Throwable throwable = record.getThrown();
    if (throwable != null) {
      throwable.printStackTrace(writer);
    }
  }

  @Override
  public void flush() {
    writer.flush();
  }

  @Override
  public void close() throws SecurityException {
    writer.close();
  }

  public StringBuffer getBuffer() {
    return stringWriter.getBuffer();
  }
}

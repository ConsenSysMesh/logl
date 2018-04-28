package org.logl.logl;

import java.util.function.Consumer;

import org.logl.Level;
import org.logl.LogMessage;
import org.logl.LogWriter;

interface LevelLogger {

  void log(Level level, LogMessage message);

  void log(Level level, CharSequence message);

  void log(Level level, LogMessage message, Throwable cause);

  void log(Level level, CharSequence message, Throwable cause);

  void logf(Level level, String format, Object... args);

  void batch(Level level, Consumer<LogWriter> fn);
}

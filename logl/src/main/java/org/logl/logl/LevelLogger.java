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

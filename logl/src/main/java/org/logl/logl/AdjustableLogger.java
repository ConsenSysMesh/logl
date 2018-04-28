package org.logl.logl;

import org.logl.Level;
import org.logl.Logger;

/**
 * A {@link Logger} that can adjust the level it logs at.
 */
public interface AdjustableLogger extends Logger {

  /**
   * Set the log level for this logger.
   *
   * @param level The log level to log at.
   * @return The previous log level.
   */
  Level setLevel(Level level);
}

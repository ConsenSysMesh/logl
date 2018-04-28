package org.logl.logl;

import java.nio.file.Path;
import java.time.Instant;

/**
 * A strategy for controlling rotation in {@link RotatingFilePrintWriterSupplier}.
 */
public interface FileRotationStrategy {

  /**
   * Obtain a strategy based on the number of bytes written to the output file.
   *
   * @param thresholdBytes The number of bytes that should trigger rotation.
   * @param delayMillis A delay, in milliseconds, after a previous rotation before another is requested.
   * @return A rotation strategy.
   */
  static FileRotationStrategy forSize(long thresholdBytes, long delayMillis) {
    return new DefaultFileRotationStrategy(thresholdBytes, delayMillis, Instant::now);
  }

  /**
   * Determine if a rotation should occur.
   *
   * @param outputFile The output file.
   * @param bytesWritten The number of bytes written to the file.
   * @return {@code true} if a rotation should occur.
   */
  boolean shouldRotate(Path outputFile, long bytesWritten);

  /**
   * Will be called to notify the strategy that rotation has been completed.
   *
   * <p>
   * This can be used to reset any timers, etc.
   */
  void rotationCompleted();
}

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

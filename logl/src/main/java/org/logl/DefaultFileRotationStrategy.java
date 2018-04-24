package org.logl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

final class DefaultFileRotationStrategy implements FileRotationStrategy {
  private final long thresholdBytes;
  private final long delayMillis;
  private final Supplier<Instant> currentTimeSupplier;
  private AtomicReference<Instant> nextRotation = new AtomicReference<>(Instant.MIN);

  DefaultFileRotationStrategy(long thresholdBytes, long delayMillis, Supplier<Instant> currentTimeSupplier) {
    this.thresholdBytes = thresholdBytes;
    this.delayMillis = delayMillis;
    this.currentTimeSupplier = currentTimeSupplier;
  }

  @Override
  public boolean shouldRotate(Path outputFile, long bytesWritten) {
    if (!Files.exists(outputFile)) {
      return true;
    }
    if (currentTimeSupplier.get().isBefore(nextRotation.get())) {
      // rotation delay timer has not expired
      return false;
    }
    return bytesWritten >= thresholdBytes;
  }

  @Override
  public void rotationCompleted() {
    if (delayMillis > 0) {
      nextRotation.set(currentTimeSupplier.get().plusMillis(delayMillis));
    }
  }
}

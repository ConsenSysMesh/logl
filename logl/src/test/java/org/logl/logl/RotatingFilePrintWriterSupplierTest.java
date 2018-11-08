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

import static java.nio.file.Files.delete;
import static java.nio.file.Files.walkFileTree;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RotatingFilePrintWriterSupplierTest {

  private Path tempDir;
  private Path logFile;
  private Path archivedLogFile[] = new Path[9];

  @BeforeEach
  void setup() throws Exception {
    tempDir = Files.createTempDirectory(getClass().getName());
    logFile = tempDir.resolve("output.log");
    for (int i = 1; i <= 9; ++i) {
      archivedLogFile[i - 1] = tempDir.resolve("output.log." + i);
    }
  }

  @AfterEach
  void cleanup() throws Exception {
    walkFileTree(tempDir, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        delete(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        delete(dir);
        return FileVisitResult.CONTINUE;
      }
    });
  }

  @Test
  void createsLog() throws Exception {
    new RotatingFilePrintWriterSupplier(logFile, FileRotationStrategy.forSize(250000, 0), 10);
    assertThat(Files.exists(logFile)).isTrue();
  }

  @Test
  void shouldRotatesLogWhenSizeExceeded() throws Exception {
    FileRotationStrategy strategy = FileRotationStrategy.forSize(10, 0);
    RotatingFilePrintWriterSupplier supplier = new RotatingFilePrintWriterSupplier(logFile, strategy, 10);

    supplier.get().write("A log line greater than the threshold");
    assertThat(Files.exists(logFile)).isTrue();
    assertThat(Files.exists(archivedLogFile[0])).describedAs("should not have rotated").isFalse();

    supplier.get().write("A log line greater than the threshold");
    assertThat(Files.exists(logFile)).isTrue();
    assertThat(Files.exists(archivedLogFile[0])).describedAs("should have rotated once").isTrue();
    assertThat(Files.exists(archivedLogFile[1])).describedAs("should not have rotated twice").isFalse();

    supplier.get().write("<10chars");
    supplier.get().write("A few more chars");
    assertThat(Files.exists(logFile)).isTrue();
    assertThat(Files.exists(archivedLogFile[0])).describedAs("should have rotated once").isTrue();
    assertThat(Files.exists(archivedLogFile[1])).describedAs("should have rotated twice").isTrue();
    assertThat(Files.exists(archivedLogFile[2])).describedAs("should not have rotated thrice").isFalse();
  }

  @Test
  void shouldLimitsTheNumberOfArchives() throws Exception {
    FileRotationStrategy strategy = FileRotationStrategy.forSize(10, 0);
    RotatingFilePrintWriterSupplier supplier = new RotatingFilePrintWriterSupplier(logFile, strategy, 2);

    supplier.get().write("A log line greater than the threshold");
    assertThat(Files.exists(logFile)).isTrue();
    assertThat(Files.exists(archivedLogFile[0])).describedAs("should not have rotated").isFalse();

    supplier.get().write("A log line greater than the threshold");
    assertThat(Files.exists(logFile)).isTrue();
    assertThat(Files.exists(archivedLogFile[0])).describedAs("should have rotated once").isTrue();
    assertThat(Files.exists(archivedLogFile[1])).describedAs("should not have rotated twice").isFalse();

    supplier.get().write("A log line greater than the threshold");
    assertThat(Files.exists(logFile)).isTrue();
    assertThat(Files.exists(archivedLogFile[0])).describedAs("should have rotated once").isTrue();
    assertThat(Files.exists(archivedLogFile[1])).describedAs("should have rotated twice").isTrue();
    assertThat(Files.exists(archivedLogFile[2])).describedAs("should not have rotated thrice").isFalse();

    supplier.get().write("A log line greater than the threshold");
    assertThat(Files.exists(logFile)).isTrue();
    assertThat(Files.exists(archivedLogFile[0])).describedAs("should have rotated once").isTrue();
    assertThat(Files.exists(archivedLogFile[1])).describedAs("should have rotated twice").isTrue();
    assertThat(Files.exists(archivedLogFile[2])).describedAs("should have discarded").isFalse();
  }

  @Test
  void shouldDelayRotation() throws Exception {
    AtomicReference<Instant> time = new AtomicReference<>(Instant.now());
    FileRotationStrategy strategy = new DefaultFileRotationStrategy(10, SECONDS.toMillis(60), time::get);
    RotatingFilePrintWriterSupplier supplier = new RotatingFilePrintWriterSupplier(logFile, strategy, 10);

    supplier.get().write("A log line greater than the threshold");
    assertThat(Files.exists(logFile)).isTrue();
    assertThat(Files.exists(archivedLogFile[0])).describedAs("should not have rotated").isFalse();

    supplier.get().write("A log line greater than the threshold");
    assertThat(Files.exists(logFile)).isTrue();
    assertThat(Files.exists(archivedLogFile[0])).describedAs("should have rotated once").isTrue();
    assertThat(Files.exists(archivedLogFile[1])).describedAs("should not have rotated twice").isFalse();

    supplier.get().write("A log line greater than the threshold");
    assertThat(Files.exists(logFile)).isTrue();
    assertThat(Files.exists(archivedLogFile[0])).describedAs("should have rotated once").isTrue();
    assertThat(Files.exists(archivedLogFile[1])).describedAs("should not have rotated twice").isFalse();

    time.set(time.get().plusSeconds(59));
    supplier.get().write("A log line greater than the threshold");
    assertThat(Files.exists(logFile)).isTrue();
    assertThat(Files.exists(archivedLogFile[0])).describedAs("should have rotated once").isTrue();
    assertThat(Files.exists(archivedLogFile[1])).describedAs("should not have rotated twice").isFalse();

    time.set(time.get().plusSeconds(1));
    supplier.get().write("A log line greater than the threshold");
    assertThat(Files.exists(archivedLogFile[0])).describedAs("should have rotated once").isTrue();
    assertThat(Files.exists(archivedLogFile[1])).describedAs("should have rotated twice").isTrue();
    assertThat(Files.exists(archivedLogFile[2])).describedAs("should not have rotated thrice").isFalse();
  }

  @Test
  void shouldListArchives() throws Exception {
    FileRotationStrategy strategy = FileRotationStrategy.forSize(10, 0);
    RotatingFilePrintWriterSupplier supplier = new RotatingFilePrintWriterSupplier(logFile, strategy, 10);

    supplier.get().write("A log line greater than the threshold");
    supplier.get().write("A log line greater than the threshold");
    supplier.get().write("A log line greater than the threshold");
    supplier.get().write("A log line greater than the threshold");

    assertThat(Files.exists(archivedLogFile[0])).isTrue();
    assertThat(Files.exists(archivedLogFile[1])).isTrue();
    assertThat(Files.exists(archivedLogFile[2])).isTrue();

    assertThat(supplier.archives()).containsExactly(archivedLogFile[0], archivedLogFile[1], archivedLogFile[2]);
  }
}

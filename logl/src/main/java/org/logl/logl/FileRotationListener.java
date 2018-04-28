package org.logl.logl;

import java.io.PrintWriter;

/**
 * A listener for rotation events in {@link RotatingFilePrintWriterSupplier}.
 */
public interface FileRotationListener {

  /**
   * Invoked when rotation of output files is completed.
   *
   * @param out A {@link PrintWriter} that can be used to write content that will be added to the log when rotation has
   *        completed.
   */
  void rotationCompleted(PrintWriter out);

  /**
   * Invoked when an exception occurs during rotation.
   *
   * @param e The exception that occurred.
   * @param out A {@link PrintWriter} that can be used to write content that will be added to the log when rotation has
   *        completed.
   */
  void rotationError(Exception e, PrintWriter out);
}

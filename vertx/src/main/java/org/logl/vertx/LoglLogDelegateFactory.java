package org.logl.vertx;

import java.util.Map;

import io.vertx.core.spi.logging.LogDelegate;
import io.vertx.core.spi.logging.LogDelegateFactory;
import org.logl.LoggerProvider;

/**
 * A <a href="http://vertx.io">Vert.x</a> {@link LogDelegateFactory} implementation backed by logl.
 *
 * <p>
 * This {@link LogDelegateFactory} logs via a statically provided {@link LoggerProvider}, which <b>must</b> be set via a
 * call to {@link #setProvider(LoggerProvider)}. Any log events received prior to setting the log provider will be
 * recorded and replayed once it is made available.
 *
 * <p>
 * It is recommended to call {@link #setProvider(LoggerProvider)} immediately upon application initialization, typically
 * in a {@code static void main(String[] args)} method.
 */
public final class LoglLogDelegateFactory implements LogDelegateFactory {

  private static final Map<String, LoglLogDelegate> delegates = new WeakValueHashMap<>();
  private static volatile LoggerProvider loggerProvider = new InteractionRecordingLoggerProvider();

  @Override
  public LogDelegate createDelegate(String name) {
    synchronized (delegates) {
      return delegates.computeIfAbsent(name, n -> new LoglLogDelegate(loggerProvider.getLogger(n)));
    }
  }

  /**
   * Set the {@link LoggerProvider} that will be used for logging by Vertx.
   *
   * @param provider The logger provider.
   */
  public static void setProvider(LoggerProvider provider) {
    synchronized (delegates) {
      LoggerProvider oldProvider = LoglLogDelegateFactory.loggerProvider;
      LoglLogDelegateFactory.loggerProvider = provider;

      delegates.forEach((name, delegate) -> delegate.setLogger(provider.getLogger(name)));

      // There is a small chance that vertx will start logging to the updated delegates before
      // all recorded interactions are replayed, resulting in out-of-order log messages. This is
      // unlikely to be of concern.
      if (oldProvider instanceof InteractionRecordingLoggerProvider) {
        ((InteractionRecordingLoggerProvider) oldProvider).replayTo(provider);
      }
    }
  }
}

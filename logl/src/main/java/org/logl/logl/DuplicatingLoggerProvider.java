package org.logl.logl;

import static org.logl.LoggerProvider.loggerName;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.logl.Logger;
import org.logl.LoggerProvider;

/**
 * A {@link LoggerProvider} for {@link Logger}s that duplicate logging interactions.
 */
public final class DuplicatingLoggerProvider implements AdjustableLoggerProvider {
  private final List<LoggerProvider> loggerProviders;
  private final Map<String, DuplicatingLogger> loggers = Collections.synchronizedMap(new WeakValueHashMap<>());

  /**
   * @param loggerProviders The providers for {@link Logger}s that should receiving interactions.
   */
  public DuplicatingLoggerProvider(LoggerProvider... loggerProviders) {
    this(Arrays.asList(loggerProviders));
  }

  /**
   * @param loggerProviders The providers for {@link Logger}s that should receiving interactions.
   */
  public DuplicatingLoggerProvider(Collection<LoggerProvider> loggerProviders) {
    this.loggerProviders = new CopyOnWriteArrayList<>(loggerProviders);
  }

  /**
   * Remove a {@link LoggerProvider} from the duplicate set.
   *
   * <p>
   * Also removes any {@link Logger}s provided from currently active {@link Logger}s that this {@link LoggerProvider}
   * provided.
   *
   * @param loggerProvider The provider to remove.
   * @return {@code true} if the logger was removed.
   */
  public boolean removeProvider(LoggerProvider loggerProvider) {
    if (!loggerProviders.remove(loggerProvider)) {
      return false;
    }
    loggers.forEach((n, l) -> l.removeLogger(loggerProvider.getLogger(n)));
    return true;
  }

  @Override
  public DuplicatingLogger getLogger(Class<?> loggingClass) {
    return getLogger(loggerName(loggingClass));
  }

  @Override
  public DuplicatingLogger getLogger(String name) {
    return loggers.computeIfAbsent(name, n -> {
      List<Logger> loggers = loggerProviders.stream().map(p -> p.getLogger(name)).collect(Collectors.toList());
      return new DuplicatingLogger(loggers);
    });
  }
}

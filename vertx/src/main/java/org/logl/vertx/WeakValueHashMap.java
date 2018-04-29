package org.logl.vertx;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class WeakValueHashMap<K, V> implements Map<K, V> {
  private final Map<K, WeakReference<V>> map = new HashMap<>();
  private final Map<Reference<V>, K> rmap = new HashMap<>();
  private ReferenceQueue<V> evictions = new ReferenceQueue<>();

  @Override
  public int size() {
    evict();
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    evict();
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    evict();
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    evict();
    return map.values().stream().anyMatch(r -> value.equals(r.get()));
  }

  @Override
  public V get(Object key) {
    evict();
    WeakReference<V> ref = map.get(key);
    return (ref == null) ? null : ref.get();
  }

  @Override
  public V put(K key, V value) {
    evict();
    return internalPut(key, value);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    evict();
    m.forEach(this::internalPut);
  }

  private V internalPut(K key, V value) {
    WeakReference<V> ref = new WeakReference<>(value, evictions);
    rmap.put(ref, key);
    WeakReference<V> old = map.put(key, ref);
    return (old == null) ? null : old.get();
  }

  @Override
  public V remove(Object key) {
    evict();
    WeakReference<V> ref = map.remove(key);
    if (ref == null) {
      return null;
    }
    rmap.remove(ref);
    return ref.get();
  }

  @Override
  public void clear() {
    // replace eviction queue as we will remove everything now
    evictions = new ReferenceQueue<>();
    rmap.clear();
    map.clear();
  }

  @Override
  public Set<K> keySet() {
    evict();
    return map.keySet();
  }

  @Override
  public Collection<V> values() {
    evict();
    return map.values().stream().map(Reference::get).filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    evict();
    return map.entrySet().stream().flatMap(e -> {
      K key = e.getKey();
      V value = e.getValue().get();
      return (value == null) ? Stream.empty() : Stream.of(new Entry<K, V>() {
        @Override
        public K getKey() {
          return key;
        }

        @Override
        public V getValue() {
          return value;
        }

        @Override
        public V setValue(V value) {
          throw new UnsupportedOperationException();
        }
      });
    }).collect(Collectors.toSet());
  }

  private void evict() {
    Reference<? extends V> ref;
    while ((ref = evictions.poll()) != null) {
      K key = rmap.get(ref);
      if (key != null) {
        map.remove(key, ref);
      }
    }
  }
}

package org.logl.vertx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WeakValueHashMapTest {

  private WeakValueHashMap<String, String> map = null;

  @BeforeEach
  void setUp() {
    map = new WeakValueHashMap<>();
  }

  @Test
  void initiallyIsEmpty() {
    assertTrue(map.isEmpty());
    assertTrue(map.entrySet().isEmpty());
  }

  @Test
  void putAndGetWorkAsExpected() {
    map.put("foo", "bar");
    assertEquals("bar", map.get("foo"));
  }

  @Test
  void sizeShowsNumbersOfEntries() {
    assertEquals(0, map.size());
    map.put("foo", "bar");
    assertEquals(1, map.size());
  }

  @Test
  void removeWorksAsExpected() {
    map.put("foo", "bar");
    assertEquals("bar", map.remove("foo"));
  }

  @Test
  void containsValueIsAbleToFindIfValueIsInMap() {
    map.put("foo", "bar");
    assertTrue(map.containsValue("bar"));
    assertFalse(map.containsValue("foobar"));
  }

}

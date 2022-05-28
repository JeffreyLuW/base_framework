package com.meide.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于链式构建Map
 *
 * @param <K>
 * @param <V>
 */
public class MapHelper<K, V> {
    HashMap<K, V> map = new HashMap<>();

    public MapHelper<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public MapHelper<K, V> putAll(Map<K, V> map) {
        map.putAll(map);
        return this;
    }

    public MapHelper<K, V> clear() {
        map.clear();
        return this;
    }

    public MapHelper<K, V> remove(K key) {
        map.remove(key);
        return this;
    }

    public HashMap<K, V> get() {
        return map;
    }

    public static void main(String[] args) {

    }
}

package org.bushido.collections.cache;

public interface CacheLoader<K, V> {

	public V load(K key);
}

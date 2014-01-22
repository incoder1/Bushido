package org.bushido.collections.cache;

/**
 * Implementor of {@code CacheLoader} is used for add new cache values during
 * cache search
 * 
 * @author Victor Gubin
 * 
 * @param <K>
 *            cache key type
 * @param <V>
 *            cache value type
 */
public interface CacheLoader<K, V> {

	public V load(K key);
}

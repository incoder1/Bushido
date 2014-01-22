/*
   This library is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library.  If not, see <http://www.gnu.org/licenses/>. 
 */
package org.bushido.collections.cache;

/**
 * Collections of this type implements cache memory block in RAM of different
 * strategies
 * 
 * @author Victor Gubin
 * 
 * @param <K>
 *            key type, for identifying cache entry
 * @param <V>
 *            cache entry type
 */
public interface Cache<K, V> {

	/**
	 * Puts entry in cache
	 * 
	 * @param key
	 *            key to be used for identify cache entry
	 * @param entry
	 *            a cached entry
	 */
	public void put(final K key, final V entry);

	/**
	 * Find entry in memory cache
	 * 
	 * @param key
	 *            the cache key
	 * @return cached entry, or null if no such element in cache
	 */
	public V find(final K key);

	/**
	 * Find entry in memory cache
	 * 
	 * @param key
	 *            the cache key
	 * @param loader
	 *            implementor would be used to add new value into cache
	 * @return cached entry, or null if no such element in cache
	 */
	public V find(final K key, CacheLoader<K, V> loader);
}
package org.bushido.collections.range;

/**
 * Associative array, which associate values with range of comparative keys
 * 
 * @param <K>
 *            type of key, must implement {@link Comparable}
 * @param <V>
 *            the value type associated with range of keys
 */
public interface RangeMap<K extends Comparable<K>, V> {

	/**
	 * Inserts range of key into map
	 * 
	 * @param range
	 *            the range of keys
	 * @param value
	 *            the value to associate with range of keys
	 * @return the inserted value of null if map already contains range which
	 *         compares to {@range} as 0
	 */
	public abstract V insert(final Range<K> range, final V value);

	/**
	 * Search for values in this map using key, if key is will be found when
	 * it's between range minimum and maximum or equals range borders
	 * 
	 * @param key
	 *            key to search
	 * @return {@link Optional} which contains search results
	 */
	public abstract Optional<V> find(K key);

	/**
	 * Checks this range map on emptiness
	 * 
	 * @return wither this range map is empty
	 */
	public abstract boolean isEmpty();

	/**
	 * Returns size of this range map
	 * 
	 * @return size of this range map
	 */
	public abstract long size();

	/**
	 * Clears this range map
	 */
	public abstract void clear();

	/**
	 * Removes values associated with the range
	 * 
	 * @param range
	 *            the range associated with value
	 * @return the value, which is removed, or {@code null} if no value
	 *         associated with provided range
	 */
	public abstract V remove(Range<K> range);

}
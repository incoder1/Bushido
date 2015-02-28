package org.bushido.collections.range;

/**
 * The result of range search
 * 
 * @author Victor Gubin
 * 
 * @param <V>
 *            the type of value associated with the range
 */
public interface Optional<V> {

	/**
	 * Returns search operation status
	 * 
	 * @return {@code true} if search was successful otherwise {@code false}
	 */
	boolean isPresent();

	/**
	 * Returns search result value
	 * 
	 * @return the search result value
	 */
	V value();

	/**
	 * Returns range associated with found value
	 * 
	 * @return the range associated found with value
	 */
	Range<?> range();
}

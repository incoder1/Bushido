package org.bushido.collections.range;

import org.apache.commons.lang.Validate;

/**
 * Range identifies set of {@link Comparable} values limited with local minimum
 * and maxim. I.e. liner sequence.
 * 
 * @author Victor Gubin
 * 
 * @param <T>
 *            the type of values, must implements {@link Comparable}
 */
public class Range<T extends Comparable<T>> implements Comparable<Range<T>> {

	/**
	 * Creates new {@code Range} limited with minimum and maximum values,
	 * including border values
	 * 
	 * @param min
	 *            range minimum value
	 * @param max
	 *            range maximum value
	 * @return new range initialized with minimum and maximum
	 * @throws NullPointerException
	 *             if minimum or maximum is null
	 * @throws IllegalArgumentException
	 *             if minimum is getter then maximum or vice versa maximum is
	 *             less then minium
	 */
	public static <K extends Comparable<K>> Range<K> create(final K min, final K max) {
		Validate.notNull(min, "Specify range minimum");
		Validate.notNull(max, "Specify range maximum");
		Validate.isTrue(max.compareTo(min) > 0, "Range minimum must be gretter then range maximum");
		return new Range<K>(min, max);
	}

	private final T min;
	private final T max;

	private Range(final T min, final T max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Checks that the {@code key} is between minimum or maximum or equals them
	 * 
	 * @param key
	 *            the value to check
	 * @return whether key int this range
	 */
	public boolean contains(T key) {
		Validate.notNull(key, "Key must be provided");
		return 0 == compareToKey(key);
	}

	/**
	 * Compares key with this range
	 * 
	 * @param key
	 *            the value to compare
	 * @return if key is getter then maximum result is getter the 0, if it less
	 *         then minimum the result is less then 0 if key is between minimum
	 *         and maximum or equals a border the result is 0
	 */
	public int compareToKey(T key) {
		Validate.notNull(key, "Key must be provided");
		int lessThenMin = key.compareTo(this.min);
		if (lessThenMin < 0) {
			return -1;
		} else {
			int laggerThenMax = key.compareTo(this.max);
			if (laggerThenMax > 0) {
				return 1;
			}
		}
		return 0; // in range
	}

	/**
	 * Checks that this range contains @{code rhs} range
	 * 
	 * @param rhs
	 *            the range to check
	 * @return {@code false} {@link #compareTo(Range)} return is less or greater
	 *         then 0 otherwise {@code true}
	 */
	public boolean contains(final Range<T> rhs) {
		Validate.notNull(rhs, "Range must be provided");
		boolean result = rhs.max.compareTo(this.max) <= 0;
		if (result) {
			result = rhs.min.compareTo(this.min) >= 0;
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((max == null) ? 0 : max.hashCode());
		result = prime * result + ((min == null) ? 0 : min.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		Range<T> other = (Range<T>) obj;
		if (max == null) {
			if (other.max != null)
				return false;
		} else if (!max.equals(other.max))
			return false;
		if (min == null) {
			if (other.min != null)
				return false;
		} else if (!min.equals(other.min))
			return false;
		return true;
	}

	/**
	 * Compares this range with another one
	 * 
	 * @param rhs
	 *            the range to compare
	 * @return 1 if {@code rhs} maximum is getter then this range maximum result
	 *         , -1 if {@code rhs} minimum less then this range minimum or 0 if
	 *         rhs.min less or equals this range min and rhs greater or equals
	 *         this range max
	 **/
	@Override
	public int compareTo(Range<T> rhs) {
		if (this.min.compareTo(rhs.min) <= 0 && this.max.compareTo(rhs.max) <= 0) {
			return -1;
		}
		if (this.max.compareTo(rhs.max) >= 0 && this.min.compareTo(rhs.min) >= 0) {
			return 1;
		}
		return 0;
	}

}

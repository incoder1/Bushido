package org.bushido.collections.range;

public interface RangeEntry<K extends Comparable<K>, V> {

	public abstract V getValue();

	public abstract void setValue(V value);

	public abstract Range<K> getRange();

}
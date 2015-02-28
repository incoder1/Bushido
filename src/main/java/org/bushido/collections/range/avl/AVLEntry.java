package org.bushido.collections.range.avl;

import org.bushido.collections.range.Range;
import org.bushido.collections.range.RangeEntry;

class AVLEntry<K extends Comparable<K>, V> implements RangeEntry<K, V> {
	private final Range<K> range;
	private V value;

	private volatile AVLEntry<K, V> left;
	private volatile AVLEntry<K, V> right;

	private byte height;

	public AVLEntry(Range<K> range, V value) {
		this.range = range;
		this.value = value;
		this.left = null;
		this.right = null;
	}

	public static <Key extends Comparable<Key>, Val> AVLEntry<Key, Val> balance(AVLEntry<Key, Val> p) {
		p.fixHeight();
		if (bFactor(p) == 2) {
			if (bFactor(p.right) < 0)
				p.right = rotateRight(p.right);
			return rotateLeft(p);
		}
		if (bFactor(p) == -2) {
			if (bFactor(p.left) > 0)
				p.left = rotateLeft(p.left);
			return rotateRight(p);
		}
		return p;
	}

	public static <Key extends Comparable<Key>, Val> AVLEntry<Key, Val> rotateRight(AVLEntry<Key, Val> p) {
		AVLEntry<Key, Val> q = p.left;
		p.left = q.right;
		q.right = p;
		p.fixHeight();
		q.fixHeight();
		return q;
	}

	public static <Key extends Comparable<Key>, Val> AVLEntry<Key, Val> rotateLeft(AVLEntry<Key, Val> q) {
		AVLEntry<Key, Val> p = q.right;
		q.right = p.left;
		p.left = q;
		q.fixHeight();
		p.fixHeight();
		return p;
	}

	static <Key extends Comparable<Key>, Val> byte height(AVLEntry<Key, Val> e) {
		return (null != e) ? e.height : 0;
	}

	static <Key extends Comparable<Key>, Val> int bFactor(AVLEntry<Key, Val> e) {
		return (null != e) ? e.bFactor() : 0;
	}

	static <Key extends Comparable<Key>, Val> AVLEntry<Key, Val> removeMin(AVLEntry<Key, Val> e) {
		if (null == e.getLeft()) {
			return e.getRight();
		}
		e.setLeft(removeMin(e.getLeft()));
		return balance(e);
	}

	static <Key extends Comparable<Key>, Val> AVLEntry<Key, Val> findMin(AVLEntry<Key, Val> e) {
		return null != e ? e.findMin() : null;
	}

	private int bFactor() {
		return height(this.getRight()) - height(this.getLeft());
	}

	private void fixHeight() {
		byte hl = height(this.left);
		byte hr = height(this.right);
		this.height = (byte) ((hl > hr ? hl : hr) + 1);
	}

	private AVLEntry<K, V> findMin() {
		return (null != getLeft()) ? getLeft().findMin() : this;
	}

	AVLEntry<K, V> getLeft() {
		return this.left;
	}

	AVLEntry<K, V> setLeft(AVLEntry<K, V> newLeft) {
		this.left = newLeft;
		return this.left;
	}

	AVLEntry<K, V> getRight() {
		return this.right;
	}

	AVLEntry<K, V> setRight(AVLEntry<K, V> newRight) {
		this.right = newRight;
		return this.right;
	}

	/* Get / Set */

	@Override
	public V getValue() {
		return this.value;
	}

	@Override
	public void setValue(V value) {
		this.value = value;
	}

	@Override
	public Range<K> getRange() {
		return this.range;
	}

}
package org.bushido.collections.range.avl;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.Validate;
import org.bushido.collections.queues.Queues;
import org.bushido.collections.queues.Stack;
import org.bushido.collections.range.Optional;
import org.bushido.collections.range.Range;
import org.bushido.collections.range.RangeMap;

/**
 * Georgy Adelson-Velsky and Landis' (AVL) tree implementation based range map
 * 
 * @author Victor Gubin
 * 
 * @param <K>
 *            type of key, must implement {@link Comparable}
 * @param <V>
 *            the value type associated with range of keys
 */
public class AVLTreeRangeMap<K extends Comparable<K>, V> implements RangeMap<K, V> {

	private final AVLRangeMapOptional<V> NOT_FOUND = new AVLRangeMapOptional<V>(null, null);

	private volatile AVLEntry<K, V> root;

	private final ReadWriteLock lock;

	/**
	 * Constructs new empty AVL Tree range map
	 */
	public AVLTreeRangeMap() {
		this.lock = new ReentrantReadWriteLock(false);
		this.root = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V insert(final Range<K> range, final V value) {
		this.lock.writeLock().lock();
		try {
			AVLEntry<K, V> entry = insertEntry(this.root, range, value);
			if (null != entry) {
				this.root = entry;
				return value;
			}
			return null;
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<V> find(K key) {
		Validate.notNull(key, "Key must be provided");
		this.lock.readLock().lock();
		try {
			AVLEntry<K, V> entry = getEntry(this.root, key);
			if (null != entry) {
				return new AVLRangeMapOptional<V>(entry.getValue(), entry.getRange());
			} else {
				return NOT_FOUND;
			}
		} finally {
			this.lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		this.lock.readLock().lock();
		try {
			return null == this.root;
		} finally {
			this.lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long size() {
		if (isEmpty()) {
			return 0L;
		}
		long result = 0;
		Stack<AVLEntry<K, V>> stack = Queues.stack();
		stack.push(this.root);
		while (!stack.isEmpty()) {
			AVLEntry<K, V> e = stack.pop();
			++result;
			if (null != e.getLeft()) {
				stack.push(e.getLeft());
			}
			if (null != e.getRight()) {
				stack.push(e.getRight());
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		this.root = null;
	}

	private AVLEntry<K, V> getEntry(AVLEntry<K, V> entry, final K key) {
		if (null == entry) {
			return null;
		}
		int compare = entry.getRange().compareToKey(key);
		if (compare < 0) {
			return getEntry(entry.getLeft(), key);
		} else if (compare > 0) {
			return getEntry(entry.getRight(), key);
		}
		return entry;
	}

	private AVLEntry<K, V> insertEntry(AVLEntry<K, V> node, Range<K> range, final V value) {
		if (null == node) {
			return new AVLEntry<K, V>(range, value);
		}
		int cmp = range.compareTo(node.getRange());
		if (cmp < 0) {
			node.setLeft(insertEntry(node.getLeft(), range, value));
		} else if (cmp > 0) {
			node.setRight(insertEntry(node.getRight(), range, value));
		} else {
			return null;
		}
		return AVLEntry.balance(node);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public V remove(Range<K> range) {
		Validate.notNull(range, " Range must be provided");
		if (!isEmpty()) {
			this.lock.writeLock().lock();
			try {
				Object result[] = new Object[1];
				AVLEntry<K, V> entry = this.removeEntry(this.root, range, result);
				this.root = entry;
				return (result[0] != null) ? (V) result[0] : null;
			} finally {
				this.lock.writeLock().unlock();
			}
		} else {
			return null;
		}
	}

	private AVLEntry<K, V> removeEntry(final AVLEntry<K, V> node, final Range<K> range, Object[] result) {
		if (node == null) {
			return null;
		}
		boolean equalRanges = node.getRange().equals(range);
		int cmp = range.compareTo(node.getRange());
		if (0 == cmp && !equalRanges) {
			return null;
		}
		if (equalRanges) {
			AVLEntry<K, V> q = node.getLeft();
			AVLEntry<K, V> r = node.getRight();
			result[0] = node.getValue();
			if (null == q) {
				return q;
			}
			AVLEntry<K, V> min = AVLEntry.findMin(r);
			min.setRight(AVLEntry.removeMin(r));
			min.setLeft(q);
			return AVLEntry.balance(min);
		}
		if (cmp < 0) {
			node.setLeft(removeEntry(node.getLeft(), range, result));
		} else if (cmp > 0) {
			node.setRight(removeEntry(node.getRight(), range, result));
		}
		return AVLEntry.balance(node);
	}

	private static final class AVLRangeMapOptional<V> implements Optional<V> {

		private final V result;
		private final Range<?> range;

		private AVLRangeMapOptional(V result, Range<?> range) {
			this.result = result;
			this.range = range;
		}

		@Override
		public boolean isPresent() {
			return result != null;
		}

		@Override
		public V value() {
			return result;
		}

		@Override
		public Range<?> range() {
			return range;
		}

	}

}

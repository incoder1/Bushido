package org.bushido.collections.skeeplist;

import java.util.AbstractList;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;


// FIXME: not working yet
public class SkeepList<E> extends AbstractList<E> {

	/**
	 * Generates the initial random seed for the cheaper per-instance random
	 * number generators used in randomLevel.
	 */
	private static final Random seedGenerator = new Random();

	/**
	 * The comparator used to maintain order in this map, or null if using
	 * natural ordering.
	 * 
	 * @serial
	 */
	private final Comparator<? super E> comparator;

	/**
	 * Seed for simple random number generator. Not volatile since it doesn't
	 * matter too much if different threads don't see updates.
	 */
	private transient int randomSeed;

	/**
	 * Head index
	 */
	private final HeadExpressLine<E> head;

	public SkeepList(Comparator<E> comparator) {
		this.comparator = comparator;
		this.head = new HeadExpressLine<E>(new Node<E>(Node.BASE_HEADER, null), null,
				null, 1);
	}

	@Override
	public E get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * If using comparator, return a ComparableUsingComparator, else cast key as
	 * Comparable, which may cause ClassCastException, which is propagated back
	 * to caller.
	 */
	@SuppressWarnings("unchecked")
	private Comparable<? super E> comparable(Object value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (comparator != null) {
			return new ComparableUsingComparator<E>((E) value, comparator);
		} else {
			return (Comparable<? super E>) value;
		}
	}

	// Nodes and indexing
	private static class Node<E> {

		public static final Object BASE_HEADER = new Object();

		private AtomicReference<Object> value;
		private AtomicReference<Node<E>> next;

		/**
		 * Creates a new regular node.
		 */
		Node(Object value, Node<E> next) {
			this.value = new AtomicReference<Object>(value);
			this.next = new AtomicReference<Node<E>>(next);
		}

		/**
		 * compareAndSet value field
		 */
		boolean casValue(E cmp, E val) {
			return this.value.compareAndSet(cmp, val);
		}

		/**
		 * compareAndSet next field
		 */
		boolean casNext(Node<E> cmp, Node<E> next) {
			return this.next.compareAndSet(cmp, next);
		}

		/**
		 * Returns true if this node is a marker. This method isn't actually
		 * called in any current code checking for markers because callers will
		 * have already read value field and need to use that read (not another
		 * done here) and so directly test if value points to node.
		 * 
		 * @param n
		 *            a possibly null reference to a node
		 * @return true if this node is a marker node
		 */
		boolean isMarker() {
			return value.get() == this;
		}

	}

	private static class ExpressLine<E> {
		final ExpressLine<E> down;
		final AtomicReference<ExpressLine<E>> right;
		final Node<E> node;

		/**
		 * Creates index node with given values.
		 */
		ExpressLine(Node<E> node, ExpressLine<E> down, ExpressLine<E> right) {
			this.node = node;
			this.down = down;
			this.right = new AtomicReference<ExpressLine<E>>(right);
		}

		/**
		 * compareAndSet right field
		 */
		final boolean casRight(ExpressLine<E> cmp, ExpressLine<E> val) {
			return right.compareAndSet(cmp, val);
		}

		/**
		 * Returns true if the node this indexes has been deleted.
		 * 
		 * @return true if indexed node is known to be deleted
		 */
		final boolean indexesDeletedNode() {
			return node.value == null;
		}

		/**
		 * Tries to CAS newSucc as successor. To minimize races with unlink that
		 * may lose this index node, if the node being indexed is known to be
		 * deleted, it doesn't try to link in.
		 * 
		 * @param succ
		 *            the expected current successor
		 * @param newSucc
		 *            the new successor
		 * @return true if successful
		 */
		final boolean link(ExpressLine<E> succ, ExpressLine<E> newSucc) {
			Node<E> n = node;
			newSucc.right.set(newSucc);
			return n.value != null && casRight(succ, newSucc);
		}

		/**
		 * Tries to CAS right field to skip over apparent successor succ. Fails
		 * (forcing a retraversal by caller) if this node is known to be
		 * deleted.
		 * 
		 * @param succ
		 *            the expected current successor
		 * @return true if successful
		 */
		final boolean unlink(ExpressLine<E> succ) {
			return !indexesDeletedNode() && casRight(succ, succ.right.get());
		}
	}

	// Comparation

	/**
	 * Nodes heading each level keep track of their level.
	 */
	static final class HeadExpressLine<E> extends ExpressLine<E> {
		private final int level;

		HeadExpressLine(Node<E> node, ExpressLine<E> down, ExpressLine<E> right, int level) {
			super(node, down, right);
			this.level = level;
		}
	}

	/**
	 * Represents a key with a comparator as a Comparable.
	 * 
	 * Because most sorted collections seem to use natural ordering on
	 * Comparables (Strings, Integers, etc), most internal methods are geared to
	 * use them. This is generally faster than checking per-comparison whether
	 * to use comparator or comparable because it doesn't require a (Comparable)
	 * cast for each comparison. (Optimizers can only sometimes remove such
	 * redundant checks themselves.) When Comparators are used,
	 * ComparableUsingComparators are created so that they act in the same way
	 * as natural orderings. This penalizes use of Comparators vs Comparables,
	 * which seems like the right tradeoff.
	 */
	static final class ComparableUsingComparator<E> implements Comparable<E> {
		private final E actual;
		private final Comparator<? super E> comparator;

		ComparableUsingComparator(final E key,final Comparator<? super E> cmp) {
			this.actual = key;
			this.comparator = cmp;
		}

		public int compareTo(final E other) {
			return comparator.compare(actual, other);
		}
	}

}

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
package org.bushido.collections;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.bushido.lang.ObjectArrayUtils;

/**
 * {@IndexSet} ordered {@link Set} implementation which uses hash table to index
 * element in memory array.
 * <p>
 * If you delete element from the set, it not bring to reordering of data
 * storage, only indexes would be changed. Data storage memory would be
 * compacted when needed. You also can do it manually using
 * {@link #rebuildIndex}
 * </p>
 * <p>
 * The {@link Set} is guaranteed to have natural iteration order.
 * </p>
 * 
 * @author Victor Gubin
 * 
 */
public class IndexedSet<T> extends AbstractSet<T> implements Cloneable,
		Serializable {

	private transient HashMap<Object, Integer> index;

	private int scaleFactor;

	private transient Object data[];

	private int last;

	/**
	 * Creates new {@code IndexedSet}
	 * 
	 * @param initialCapacity
	 *            the initial data storage size
	 * @param scaleFactor
	 *            step which will be used to increase capacity if necessary
	 */
	public IndexedSet(final int initialCapacity, final int scaleFactor) {
		if (initialCapacity <= 0) {
			throw new IllegalArgumentException("Illegal Capacity: "
					+ initialCapacity);
		}
		this.scaleFactor = scaleFactor;
		this.index = new HashMap<Object, Integer>();
		this.data = new Object[initialCapacity];
		this.last = -1;
	}

	/**
	 * Construct new {@code IndexSet} with default capacity size and scaleFactor
	 * 
	 * @see IndexedSet#IndexedSet(int, int)
	 */
	public IndexedSet() {
		this(10, 10);
	}

	/**
	 * Return current set capacity, e.g. data storage array length
	 * 
	 * @return
	 */
	public int getCapacity() {
		return data.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return new IndexSetIterator<T>(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return this.index.size();
	}

	/**
	 * Add new element into set. Rebuild index if new set size overflows current
	 * capacity.
	 * 
	 * @param e
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	public boolean add(T e) {
		if (this.contains(e)) {
			return false;
		}
		if (last + 1 > data.length - 1) {
			if (0 == rebuildIndex()) {
				this.resize();
			}
		}
		this.index.put(e, ++last);
		this.data[last] = e;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return index.containsKey(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		Integer ind = index.remove(o);
		if (null != ind) {
			data[ind] = null;
		}
		return null != ind;
	}

	/**
	 * Remove elements, which were deleted from the index also from the data
	 * storage (e.g. clear garbage) then rebuild index according new element
	 * positions in data storage.
	 * <p>
	 * Avoid using this method rapidly, it is time costing.
	 * </p>
	 * 
	 * @return number of removed elements
	 */
	public int rebuildIndex() {
		final int result = defragment();
		if (0 == result) {
			return result;
		}
		index.clear();
		for (int i = 0; (i < data.length) && (data[i] != null); i++) {
			index.put(data[i], i);
		}
		last = index.size() - 1;
		return result;
	}

	/**
	 * Defragment - compact data storage array
	 */
	private int defragment() {
		ObjectArrayUtils.compact(this.data);
		return data.length - index.size();
	}

	/**
	 * Resize storage data, to allocate @{code scaleFactor} more space.
	 */
	private void resize() {
		this.data = Arrays.copyOf(data, data.length + scaleFactor);
	}

	/**
	 * Save the state of the {@code IndexedSet} instance to a stream (that is,
	 * serialize it).
	 * 
	 * @serialData The length of the array backing the <tt>ArrayList</tt>
	 *             instance is emitted (int), followed by all of its elements
	 *             (each an <tt>Object</tt>) in the proper order.
	 */
	private void writeObject(final ObjectOutputStream s)
			throws java.io.IOException {
		// defragment data storage before saving
		this.defragment();
		// Write out any hidden serialization magic
		s.defaultWriteObject();
		s.writeInt(this.scaleFactor);
		s.writeInt(this.last);
		for (int i = 0; i < last + 1; i++) {
			s.writeObject(data[i]);
		}
	}

	/**
	 * Reconstitute the {@code IndexedSet} instance from a stream (that is,
	 * deserialize it).
	 */
	private void readObject(final ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in any hidden serialization magic
		s.defaultReadObject();
		this.scaleFactor = s.readInt();
		this.last = s.readInt();
		this.data = new Object[this.last + 1];
		for (int i = 0; i < data.length; i++) {
			this.data[i] = s.readObject();
		}
		this.index = new HashMap<Object, Integer>();
		for (int i = 0; (i < data.length) && (data[i] != null); i++) {
			index.put(data[i], i);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object clone() throws CloneNotSupportedException {
		final IndexedSet<T> clone = (IndexedSet<T>) super.clone();
		clone.index = (HashMap<Object, Integer>) index.clone();
		clone.data = Arrays.copyOf(this.data, this.data.length);
		return clone;
	}

	/**
	 * Iterator over data
	 */
	private static class IndexSetIterator<T> implements Iterator<T> {

		private int current;

		private final IndexedSet<T> ref;

		/**
		 * @param ref
		 */
		private IndexSetIterator(final IndexedSet<T> reference) {
			this.ref = reference;
			this.current = -1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			int next = this.current + 1;
			boolean result = false;
			if (next < ref.data.length) {
				for (int i = next; i < ref.data.length; i++) {
					if (ref.index.containsKey(ref.data[i])) {
						result = true;
						break;
					}
				}
			}
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#next()
		 */
		@Override
		// Type unsafe cast is required hear
		@SuppressWarnings("unchecked")
		public T next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}
			for (int i = ++current; i < ref.data.length; i++) {
				if (null != ref.data[i]) {
					current = i;
					break;
				}
			}
			return (T) ref.data[current];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			ref.index.remove(ref.data[current]);
			ref.data[current] = null;
		}

	};

	private static final long serialVersionUID = 3577316740290696612L;
}

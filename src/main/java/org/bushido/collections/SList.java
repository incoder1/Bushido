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

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Single-linked list implementation of the {@code List}. Implements all
 * optional list operations, and permits all elements (including {@code null}).
 * 
 * @author Victor Gubin
 * 
 * @param <E>
 *            the type of elements held in this collection
 */
public class SList<E> extends AbstractSequentialList<E> {

	private final SNode<E> begin;

	/**
	 * Constructing new empty {@code SList}
	 */
	public SList() {
		super();
		this.begin = new SNode<E>(null, null);
	}

	/**
	 * Constructs a list containing the elements of the specified collection, in
	 * the order they are returned by the collection's iterator.
	 * 
	 * @param c
	 *            the collection whose elements are to be placed into this list
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public SList(final Collection<E> collecion) {
		this();
		this.addAll(-1, collecion);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E set(int index, E element) {
		SListIterator<E> it = listIterator(index);
		E result = it.next();
		it.set(element);
		return result;
	}

	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * <p>
	 * This implementation first gets a list iterator pointing to the indexed
	 * element (with <tt>listIterator(index)</tt>). Then, it gets the element
	 * using <tt>ListIterator.next</tt> and returns it.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public E get(int index) {
		try {
			return listIterator(index).next();
		} catch (NoSuchElementException exc) {
			throw new IndexOutOfBoundsException("Index: " + index);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		int result = 0;
		for (Iterator<E> it = iterator(); it.hasNext(); it.next()) {
			++result;
		}
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		this.begin.setNext(null);
	}

	/**
	 * Check iterator validity in case of scrolling operation
	 * 
	 * @param it
	 *            iterator to check
	 * @param index
	 *            index to be used for constructing error message
	 */
	protected void checkIterator(final SListIterator<E> it, int index) {
		if (!it.hasNext()) {
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SListIterator<E> iterator() {
		return new SListIterator<E>(this.begin);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SListIterator<E> listIterator(int index) {
		SListIterator<E> result = iterator();
		for (int i = -1; i < index - 1; i++) {
			checkIterator(result, index);
			result.next();
		}
		return result;
	}

	/**
	 * Returns the index of the first occurrence of the specified element in
	 * this list, or -1 if this list does not contain the element. More
	 * formally, returns the lowest index {@code i} such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 * 
	 * @param o
	 *            element to search for
	 * @return the index of the first occurrence of the specified element in
	 *         this list, or -1 if this list does not contain the element
	 */
	@Override
	public int indexOf(Object el) {
		int result = -1;
		for (SListIterator<E> it = iterator(); it.hasNext();) {
			if (el.equals(it.next())) {
				result = it.thisIndex();
				break;
			}
		}
		return result;
	}

	/**
	 * Constructs an IndexOutOfBoundsException detail message. Of the many
	 * possible refactorings of the error handling code, this "outlining"
	 * performs best with both server and client VMs.
	 */
	private static String outOfBoundsMsg(int index) {
		return "Index: " + index;
	}

	public class SListIterator<EL> implements ListIterator<EL> {
		private int index;
		private SNode<EL> prev;
		private SNode<EL> position;

		private SListIterator(final SNode<EL> position) {
			this.prev = null;
			this.position = position;
			this.index = -1;
		}

		@Override
		public boolean hasNext() {
			return position.getNext() != null;
		}

		@Override
		public EL next() {
			EL result = null;
			if (hasNext()) {
				this.prev = this.position;
				this.position = position.getNext();
				result = this.position.getValue();
				++this.index;
			}
			return result;
		}

		@Override
		public void remove() {
			this.prev.setNext(this.position.getNext());
			this.position = this.position.getNext();
		}

		@Override
		public void add(EL element) {
			this.position.setNext(new SNode<EL>(this.position.getNext(),
					element));
			this.prev = this.position;
			this.position = this.position.getNext();
		}

		@Override
		public void set(EL element) {
			this.position = new SNode<EL>(this.position.getNext(), element);
			this.prev.setNext(this.position);
		}

		@Override
		public boolean hasPrevious() {
			return this.prev != null;
		}

		@Override
		public EL previous() {
			return this.prev.getValue();
		}

		@Override
		public int nextIndex() {
			return this.index + 1;
		}

		protected int thisIndex() {
			return this.index;
		}

		@Override
		public int previousIndex() {
			return this.index - 1;
		}

	}

	private static final class SNode<EL> {
		private SNode<EL> next;
		private final EL value;

		SNode(SNode<EL> next, EL value) {
			this.next = next;
			this.value = value;
		}

		public void setNext(SNode<EL> next) {
			this.next = next;
		}

		public SNode<EL> getNext() {
			return next;
		}

		public EL getValue() {
			return value;
		}
	}

}

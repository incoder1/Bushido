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
package org.bushido.collections.queues;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Tread safe single linked list based {@link Stack} implementation.
 * 
*  Treiber stack implementation
 * 
 * @author Victor_Gubin
 * 
 * @param <T>
 *            stack element type
 */
class LinkedStack<T> implements Stack<T> {

	private AtomicReference< Entry<T> > head;

	public LinkedStack() {
		this.head = new AtomicReference<Entry<T>>(null);
	}

	@Override
	public void push(final T element) {
		Entry<T> newHead = new Entry<T>(element, null);
        while (true) {
            Entry<T> oldHead = this.head.get();
            newHead.setNext(oldHead);
            if (head.compareAndSet(oldHead, newHead)) {
                break;
            }
        }
	}

	@Override
	public T pop() {
		while (true) {
			Entry<T> oldHead = this.head.get();
            Entry<T> newHead = oldHead.getNext();
            if (head.compareAndSet(oldHead, newHead)) {
                return oldHead.getValue();
            }
        }
	}

	@Override
	public T peek() {
		T result = null;
		if (null != this.head.get()) {
			result = this.head.get().getValue();
		}
		return result;
	}

	@Override
	public long size() {
		long result = 0;
		for (Entry<T> it = this.head.get(); it != null; it = it.getNext()) {
			++result;
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		return null == this.head.get();
	}

	@Override
	public void clean() {
		// current head is lost, objects should be garbage collected
		this.head.compareAndSet(this.head.get(), null);
	}

	private static final class Entry<T> {
		private final T value;
		private Entry<T> next;

		public Entry(final T value, final Entry<T> next) {
			this.value = value;
			this.next = next;
		}

		public T getValue() {
			return value;
		}

		public Entry<T> getNext() {
			return next;
		}
		
		public void setNext(final Entry<T> newNext) {
			this.next = newNext;
		}

	}

}

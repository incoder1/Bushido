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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LinkedQueue<T> implements Queue<T> {

	private final Lock lock;

	private volatile Entry<T> head;
	private volatile Entry<T> tail;

	LinkedQueue() {
		this.lock = new ReentrantLock(true);
		this.head = null;
		this.tail = null;
	}

	@Override
	public boolean isEmpty() {
		return this.head == null;
	}

	@Override
	public long size() {
		long result = 0;
		for (Entry<T> it = this.head; it != null; it = it.getPrev()) {
			++result;
		}
		return result;
	}

	@Override
	public void clean() {
		lock.lock();
		try {
			this.tail = null;
			this.head = this.tail;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void offer(T element) {
		if (null == this.head) {
			lock.lock();
			try {
				if (null == this.head) {
					this.tail = new Entry<T>(element);
					this.head = tail;
				} else {
					final Entry<T> prevTail = this.tail;
					this.tail = new Entry<T>(element);
					prevTail.setPrev(this.tail);
				}
			} finally {
				lock.unlock();
			}
		} else {
			offerNotEmpty(element);
		}
	}

	private final void offerNotEmpty(T element) {
		lock.lock();
		try {
			final Entry<T> prevTail = this.tail;
			this.tail = new Entry<T>(element);
			prevTail.setPrev(this.tail);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public T poll() {
		T result = null;
		if (this.head != null) {
			lock.lock();
			try {
				if (this.head != null) {
					result = this.head.getValue();
					this.head = this.head.getPrev();
				}
			} finally {
				lock.unlock();
			}
		}
		return result;
	}

	@Override
	public T peek() {
		T result = null;
		if (this.head != null) {
			lock.lock();
			try {
				result = this.head.getValue();
			} finally {
				lock.unlock();
			}
		}
		return result;
	}

	private static final class Entry<T> {
		private final T value;
		private Entry<T> prev;

		private Entry(T value) {
			super();
			this.value = value;
			this.prev = null;
		}

		public Entry<T> getPrev() {
			return prev;
		}

		public void setPrev(Entry<T> prev) {
			this.prev = prev;
		}

		public T getValue() {
			return value;
		}

	}

}

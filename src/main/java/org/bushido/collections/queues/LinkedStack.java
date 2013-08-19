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

class LinkedStack<T> implements Stack<T> {

	private final Lock lock;

	private Entry<T> head;

	public LinkedStack() {
		this.lock = new ReentrantLock(true);
		this.head = null;
	}

	@Override
	public void push(final T element) {
		lock.lock();
		try {
			this.head = new Entry<T>(element, this.head);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public T pop() {
		T result = null;
		if (!isEmpty()) {
			lock.lock();
			try {
				result = this.head.getValue();
				this.head = this.head.getNext();
			} finally {
				lock.unlock();
			}
		}
		return result;
	}

	@Override
	public T peek() {
		lock.lock();
		try {
			return this.head.getValue();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public long size() {
		long result = 0;
		lock.lock();
		try {
			for (Entry<T> it = this.head; it != null; it = it.getNext()) {
				++result;
			}
		} finally {
			lock.unlock();
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		lock.lock();
		try {
			return null == this.head;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void clean() {
		lock.lock();
		try {
			this.head = null;
		} finally {
			lock.unlock();
		}
	}

	private static final class Entry<T> {
		private final T value;
		private final Entry<T> next;

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

	}

}

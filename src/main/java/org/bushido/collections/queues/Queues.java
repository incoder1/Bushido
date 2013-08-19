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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class consists exclusively of static methods that returns queues
 **/
public final class Queues {
	private Queues() {
	}

	/**
	 * Creates a last-in-first-out (LIFO) queue i.e. {@link Stack} implemented
	 * like the single linked list
	 * 
	 * @return new {@link Stack} instance
	 */
	public static <T> Stack<T> stack() {
		return new LinkedStack<T>();
	}

	/**
	 * Creates a first-in-first-out (FIFO) {@link Queue} implemented like the
	 * single linked list
	 * 
	 * @return new {@link Queue} instance
	 */
	public static <T> Queue<T> fifoQueue() {
		return new LinkedQueue<T>();
	}

	/**
	 * Creates array based Deque, can be used like FIFO or LIFO queue
	 * 
	 * @return new {@code Deque} instance
	 */
	public static <E> Deque<E> arrayDeque() {
		return new ArrayDeque<E>();
	}

	/**
	 * Creates array based {@link Deque} , can be used like FIFO or LIFO queue
	 * 
	 * @return new {@link Deque} instance
	 */
	public static <E> Deque<E> arrayDeque(int initialCapacity) {
		return new ArrayDeque<E>(initialCapacity);
	}

	/**
	 * Creates double linked list based concurrent {@link Deque}, can be used
	 * like FIFO or LIFO queue
	 * 
	 * @return new {@link Deque} instance
	 */
	public static <E> Deque<E> concurrentLinkedDeque() {
		return new ConcurrentLinkedDeque<E>();
	}
}

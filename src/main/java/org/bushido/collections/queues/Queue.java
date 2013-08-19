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

/**
 * Represents a first-in-first-out (FIFO) queue of objects.
 *
 * @param <T> type of queue objects
 */
public interface Queue<T> extends AbstractQueue<T> {
	/**
	 * Inserts the specified element into end of this queue
	 * 
	 * @param element
	 *            the element to add
	 */
	public void offer(T element);

	/**
	 * Retrieves and removes the head of this queue, or returns <tt>null</tt> if
	 * this queue is empty.
	 * 
	 * @return the head of this queue, or <tt>null</tt> if this queue is empty
	 */
	public T poll();

	/**
	 * Retrieves, but does not remove, the head of this queue, or returns
	 * <tt>null</tt> if this queue is empty.
	 * 
	 * @return the head of this queue, or <tt>null</tt> if this queue is empty
	 */
	public T peek();
}

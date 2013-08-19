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
 * {@code Stack} represents a last-in-first-out (LIFO) stack of objects.
 * 
 * @param <T> type of stack objects
 */
public interface Stack<T> extends AbstractQueue<T> {

	/**
	 * Pushes an item onto the top of this stack.
	 * 
	 * @param element
	 *            item to be pushed onto this stack
	 */
	public void push(final T element);

	/**
	 * Removes the object at the top of this stack and returns that object as
	 * the value of this function.
	 * 
	 * @return The object at the top of this stack
	 */
	public T pop();

	/**
	 * Looks at the object at the top of this stack without removing it from the
	 * stack.
	 * 
	 * @return the object at the top of this stack
	 */
	public T peek();
}

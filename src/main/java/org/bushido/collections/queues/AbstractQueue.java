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
 * A collection designed for holding elements prior to processing.
 * 
 * @author Victor Gubin
 * 
 * @param <T> queue element type
 */
public interface AbstractQueue<T> {
	/**
	 * Tests if this queue is empty.
	 * 
	 * @return <code>true</code> if and only if this queue contains no items;
	 *         <code>false</code> otherwise.
	 */
	public boolean isEmpty();

	/**
	 * Returns the number of components in this queue
	 */
	public long size();

	/**
	 * Clean this queue
	 */
	public void clean();
	
}

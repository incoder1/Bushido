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
package org.bushido.collections.cache;


class CacheEntry<K, V> implements Comparable<CacheEntry<K, V>> {

	private long accessTime;

	private final K key;

	private final V value;

	CacheEntry(final K key, final V value) {
		this.accessTime = System.nanoTime();
		this.key = key;
		this.value = value;
	}

	public long getAccessTime() {
		return accessTime;
	}

	public void touch() {
		this.accessTime = System.nanoTime();
	}

	@Override
	public int compareTo(final CacheEntry<K, V> o) {
		return Long.compare(this.accessTime, o.getAccessTime());
	}

	public V getValue() {
		return value;
	}

	public K getKey() {
		return key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		CacheEntry<K, V> other = (CacheEntry<K, V>) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
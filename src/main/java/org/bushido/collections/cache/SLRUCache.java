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

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides Segmented Least Recently Used (SLRU) cache memory block
 * 
 * @author Victor Gubin
 * 
 * @param <K>
 *            type of entry key identifier
 * @param <V>
 *            type of entry value
 */
public class SLRUCache<K, V> implements Cache<K, V> {

	private final Segment<K, V> trialSegment;
	private final Segment<K, V> protectedSegment;

	public SLRUCache(final int maxTrial, final int maxProtected) {
		this.trialSegment = new Segment<K, V>(maxTrial);
		this.protectedSegment = new Segment<K, V>(maxProtected);
	}

	@Override
	public void put(final K key, final V value) {
		if (trialSegment.canPut()) {
			trialSegment.put(new CacheEntry<K, V>(key, value));
		} else {
			trialSegment.extrude();
			trialSegment.put(new CacheEntry<K, V>(key, value));
		}
	}

	@Override
	public V find(final K key) {
		CacheEntry<K, V> result = this.protectedSegment.get(key);
		if (null == result) {
			result = this.trialSegment.get(key);
			if (null != result) {
				this.trialSegment.remove(result);
				if (protectedSegment.canPut()) {
					this.protectedSegment.put(result);
				} else {
					final CacheEntry<K, V> lru = this.protectedSegment.extrude();
					trialSegment.put(lru);
					protectedSegment.put(result);
				}
				protectedSegment.touch(result);
			}
		
		}
		return null != result ? result.getValue() : null;
	}
	
	@Override
	public V find(K key,final CacheLoader<K, V> loader) {
		if(null == loader) {
			throw new IllegalArgumentException("Loader can not be null");
		}
		if(null == this.find(key)) {
			this.put(key, loader.load(key));
		}
		return null;
	}

	public int trialSize() {
		return this.trialSegment.size();
	}

	public int protectedSize() {
		return this.protectedSegment.size();
	}

	private static final class Segment<K, V> {
		private final Map<K, CacheEntry<K, V>> segment;
		private final SortedSet<CacheEntry<K, V>> index;
		private final int maxSize;
		private AtomicInteger size;

		public Segment(int maxSize) {
			this.segment = new ConcurrentHashMap<K, CacheEntry<K, V>>(maxSize);
			this.index = Collections.synchronizedSortedSet(new TreeSet<CacheEntry<K, V>>());
			this.maxSize = maxSize;
			this.size = new AtomicInteger();
		}

		public void put(CacheEntry<K, V> entry) {
			this.segment.put(entry.getKey(), entry);
			this.index.add(entry);
			this.size.incrementAndGet();
		}

		public CacheEntry<K, V> get(K key) {
			final CacheEntry<K, V> result = this.segment.get(key);
			return result;
		}

		public void touch(final CacheEntry<K, V> entry) {
			this.index.remove(entry);
			entry.touch();
			this.index.add(entry);
		}

		public void remove(CacheEntry<K, V> entry) {
			this.segment.remove(entry.getKey());
			this.index.remove(entry);
			this.size.decrementAndGet();
		}

		public boolean canPut() {
			return maxSize >= (this.size.get() + 1);
		}

		public int size() {
			return this.size.get();
		}

		public CacheEntry<K, V> extrude() {
			// extrude last recently used
			final CacheEntry<K, V> result = index.first();
			this.remove(result);
			return result;
		}
	}
}
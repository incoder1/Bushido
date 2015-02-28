package org.bushido.collections;

import org.bushido.collections.cache.Cache;
import org.bushido.collections.cache.CacheLoader;
import org.bushido.collections.cache.slru.SLRUCache;


public class CacheDemo {

	private static String ARRAY[] = { "A", "B", "C", "D", "F", "I", "G" };

	public void execute() {
		SLRUCache<Integer, String> slruCache = new SLRUCache<>(3, 2);
		final Cache<Integer, String> cache = slruCache;
		for (int i = 0; i < 7; i++) {
			for (int j = i; j < 7; j++) {
				Integer key = Integer.valueOf(j);
				String value = cache.find(key,
						new CacheLoader<Integer, String>() {
							@Override
							public String load(Integer key) {
								System.out.println("Updateing cache for key="
										+ key + " with value="
										+ ARRAY[key.intValue()]);
								return ARRAY[key.intValue()];
							}
						});
				System.out.println(value);
			}
		}

	}

	public static void main(String[] args) {
		new CacheDemo().execute();
	}

}

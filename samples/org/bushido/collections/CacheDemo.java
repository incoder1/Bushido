package org.bushido.collections;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bushido.collections.cache.Cache;
import org.bushido.collections.cache.SLRUCache;

public class CacheDemo {

	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		final Cache<String, Double> cache = new SLRUCache<String, Double>(10,
				100);
		for (int i = 0; i < 500; i++) {
			pool.submit(new Runnable() {
				public void run() {
					Random rnd = new Random(System.nanoTime());
					double gausian = rnd.nextGaussian();
					final String key = String.valueOf(gausian);
					if (null != cache.find(key)) {
						System.out.println(cache.find(key));
					} else {
						cache.put(key, gausian);
					}
				}
			});
		}
		pool.shutdown();
	}

}

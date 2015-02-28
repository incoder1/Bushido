package org.bushido.collections.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.bushido.collections.cache.slru.SLRUCache;
import org.junit.Test;

public class SLRUCacheTestCase {

	private final String[] TEST_DATA = { "000", "111", "222", "333", "444",
			"555", "666", "777" };

	@Test
	public void testFunctional() throws Exception {
		final SLRUCache<Integer, String> cache = new SLRUCache<Integer, String>(
				5, 2);
		for (int i = 0; i < TEST_DATA.length; i++) {
			cache.put(i, TEST_DATA[i]);
		}
		assertNull("No LRU extrude fom trial", cache.find(0));
		assertNotNull("Wrong element extruded", cache.find(3));
		for (int i = 2; i < TEST_DATA.length; i++) {
			cache.find(i);
		}
		assertEquals("Protected not working", TEST_DATA[7], cache.find(7));
		assertEquals("Not returned from protected", TEST_DATA[3], cache.find(3));
	}

	@Test
	public void testConcurentUssage() throws Exception {
		final Cache<Integer, String> cache = new SLRUCache<Integer, String>(40,
				10);
		final ExecutorService threadPool = Executors.newFixedThreadPool(10);
		final Random rnd = new Random();
		final List<String> data = Collections
				.synchronizedList(new ArrayList<String>(50));
		byte buffer[] = new byte[100];
		for (int i = 1; i < 10; i++) {
			for (int j = 0; j < 5; j++) {
				rnd.setSeed(System.nanoTime());
				rnd.nextBytes(buffer);
				final String rndStr = new String(buffer, 0, buffer.length,
						Charset.defaultCharset());
				data.add(rndStr);
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						cache.put(rndStr.hashCode(), rndStr);
					}
				});
			}
			Thread.sleep(3);
			final AtomicInteger thisI = new AtomicInteger(i);
			for (int j = 0; j < 5; j++) {
				rnd.setSeed(System.nanoTime());
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						int index = rnd.nextInt(thisI.get());
						data.get(index);
					}
				});
			}
		}
		threadPool.shutdown();
		assertTrue("Deadlock detected",
				threadPool.awaitTermination(10, TimeUnit.MINUTES));
	}

}

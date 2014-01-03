package org.bushido.collections;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.bushido.collections.queues.Queue;
import org.bushido.collections.queues.Queues;
import org.junit.Test;

public class QueueTest {

	@Test
	public void testConstructedEmpty() {
		final Queue<Integer> queue = Queues.fifoQueue();
		assertTrue("Contructed empty", queue.isEmpty());
	}

	@Test
	public void testFIFO() {
		final Queue<Integer> queue = Queues.fifoQueue();
		for (int i = 0; i < 5; i++) {
			queue.offer(i);
		}
		Integer prevPoll = queue.poll();
		while (!queue.isEmpty()) {
			Integer pooled = queue.poll();
			assertEquals("Wrong sequence",
					Integer.valueOf(prevPoll.intValue() + 1), pooled);
			prevPoll = pooled;
		}
	}

	@Test
	public void testClean() {
		final Queue<Integer> queue = Queues.fifoQueue();
		for (int i = 0; i < 5; i++) {
			queue.offer(i);
		}
		queue.clean();
		assertTrue("Not cleared", queue.isEmpty());
	}
	
	@Test
	public void testSize() {
		final Queue<Integer> queue = Queues.fifoQueue();
		for (int i = 0; i < 100; i++) {
			queue.offer(i);
		}
		assertEquals("Wrong size",100, queue.size());
	}
	
	@Test
	public void shouldThreadSafe() throws Exception {
		final Queue<Integer> queue = Queues.fifoQueue();
		ExecutorService treadPool = Executors.newFixedThreadPool(2);
		final AtomicInteger count = new AtomicInteger(-1);
		final Callable<Void> pushRoutine = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				queue.offer(count.incrementAndGet());
				// now another thread should wait a lock until peek release it
				assertEquals("Synchronization not working", Integer.valueOf(count.incrementAndGet()), queue.peek());
				return null;
			}
		};
		treadPool.invokeAll(Collections.nCopies(5, pushRoutine));
		treadPool.shutdown();
	}

}

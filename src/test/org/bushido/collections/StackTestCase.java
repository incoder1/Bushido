package org.bushido.collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.bushido.collections.queues.Queues;
import org.bushido.collections.queues.Stack;
import org.junit.Before;
import org.junit.Test;

public class StackTestCase {

	private Stack<Integer> linkedStack;

	@Before
	public void setUp() throws Exception {
		linkedStack = Queues.stack();
		for (int i = 0; i < 5; i++) {
			linkedStack.push(i);
		}
	}

	@Test
	public void shouldConstructEmptyStack() {
		Stack<Integer> emptyStack = Queues.stack();
		assertTrue("Wrong constructed stack", emptyStack.isEmpty());
		assertEquals("Wrong size of empty stack", 0, emptyStack.size());
	}

	@Test
	public void shouldSizeCalculation() throws Exception {
		assertEquals("Push not working correctly", 5, linkedStack.size());
	}

	@Test
	public void shouldPop() throws Exception {
		assertEquals("Pop not working", Integer.valueOf(4), linkedStack.pop());
		assertEquals("Size not updated after pop", 4, linkedStack.size());
	}

	@Test
	public void shouldClean() throws Exception {
		linkedStack.clean();
		assertTrue("Clean not working", linkedStack.isEmpty());
	}

	@Test
	public void shouldThreadSafe() throws Exception {
		final Stack<Integer> stack = Queues.stack();
		ExecutorService treadPool = Executors.newFixedThreadPool(2);
		final AtomicInteger count = new AtomicInteger(-1);
		final Callable<Void> pushRoutine = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				stack.push(count.incrementAndGet());
				assertEquals("Synchronization not working", Integer.valueOf(count.get()), stack.peek());
				return null;
			}
		};
		treadPool.invokeAll(Collections.nCopies(5, pushRoutine));
		treadPool.shutdown();
	}
}

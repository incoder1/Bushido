package org.bushido.collections;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

// Recreate accordig the SkeepList
public class SortedLinkedCollectionDemo {

	private static final int TEST_COUNT = 1000;

	public static void main(String[] args) {
		SortedLinkedCollectionDemo demo = new SortedLinkedCollectionDemo();
		demo.testSkeepListSet();
		//demo.testSortedCollection();
	}

	private final Integer randomData[];

	SortedLinkedCollectionDemo() {
		this.randomData = new Integer[TEST_COUNT];
		final Random rnd = new Random(System.nanoTime());
		for (int i = 0; i < TEST_COUNT; i++) {
			rnd.setSeed(System.nanoTime() + i);
			randomData[i] = rnd.nextInt();
		}
	}

	public long invokeWithTime(final Runnable routine) {
		long start = System.nanoTime();
		routine.run();
		return System.nanoTime() - start;
	}

	private void testSkeepListSet() {
		final ConcurrentSkipListSet<Integer> set = new ConcurrentSkipListSet<>();
		long time = invokeWithTime(new Runnable() {
			public void run() {
				set.addAll(Arrays.asList(randomData));
			}
		});
		System.out.println(set.size());
		System.out.println("SkeepListSet time: "
				+ TimeUnit.NANOSECONDS.toMillis(time) + " mils");
	}

//	private void testSortedCollection() {
//		final SkeepList<Integer> sorted = new SkeepList<>();
//		long time = invokeWithTime(new Runnable() {
//			public void run() {
//				sorted.addAll(Arrays.asList(randomData));
//			}
//		});
//		System.out.println(sorted.size());
//		System.out.println("SortedCollection time: "
//				+ TimeUnit.NANOSECONDS.toMillis(time) + " mils");
//	}

}

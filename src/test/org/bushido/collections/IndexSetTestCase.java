package org.bushido.collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

/**
 * Test {@link IndexedSet} functionality
 * 
 * @author Victor Gubin
 */
public class IndexSetTestCase {

	/**
	 * Check container compatibility with {@link java.land.Set}
	 * 
	 * @throws Exception
	 */
	@Test
	public void setCompatibilityTest() throws Exception {
		final Set<TestBean> set = new IndexedSet<TestBean>();
		TestBean bean = new TestBean();
		set.add(bean);
		TestBean bean1 = (TestBean) bean.clone();
		bean1.setStringField("String2");
		set.add(bean1);
		// be shore one elements in set
		set.add((TestBean) bean.clone());

		assertTrue("Set not containing nesseary element", set.contains(bean));
		assertTrue("Set not containing nesseary element", set.contains(bean1));

		assertTrue("Element wasn't removed", set.remove(bean1));
		assertFalse("Element wasn't removed", set.contains(bean1));
		assertEquals("Set size not is valid", 1, set.size());
	}

	/**
	 * Check that elements in set is natural ordered
	 * 
	 * @throws Exception
	 */
	@Test
	public void naturalOrederTest() throws Exception {
		final Set<Integer> set = new IndexedSet<Integer>();
		int i = 0;
		for (i = 0; i < 10; i++) {
			set.add(i);
		}
		i = 0;
		for (Iterator<Integer> it = set.iterator(); it.hasNext(); i++) {
			assertEquals("Order is not natural", i, it.next().intValue());
		}
		for (i = 2; i < 10; i += 2) {
			set.remove(i);
		}
		final Integer order[] = { 0, 1, 3, 5, 7, 9 };
		assertTrue("Order destoyed by removal", Arrays.equals(order, set.toArray()));
	}

	@Test
	public void resizeDataStorageTest() throws Exception {
		final IndexedSet<Integer> set = new IndexedSet<Integer>(5, 5);
		// resize is called automatically
		for (int i = 0; i < 10; i++) {
			set.add(i);
		}
		assertEquals("Resize of data storage failed", 10, set.getCapacity());
	}

	@Test
	public void rebuildIndexTest() throws Exception {
		final IndexedSet<Integer> set = new IndexedSet<Integer>(10, 5);
		for (int i = 0; i < 10; i++) {
			set.add(i);
		}
		for (int i = 2; i < 10; i += 2) {
			set.remove(i);
		}
		set.rebuildIndex();
		final Integer expectedAfterRebuildIndex[] = { 0, 1, 3, 5, 7, 9 };
		assertTrue("Order destoyed by compacting", Arrays.equals(expectedAfterRebuildIndex, set.toArray()));
		for (int i = 10; i < 15; i++) {
			set.add(i);
		}
		final Integer expectedAfterAdd[] = { 0, 1, 3, 5, 7, 9, 10, 11, 12, 13, 14 };
		assertTrue("Order destoyed by index rebuild", Arrays.equals(expectedAfterAdd, set.toArray()));
	}

}

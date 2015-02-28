package org.bushido.collections.range;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bushido.collections.range.avl.AVLTreeRangeMap;
import org.junit.Test;

public class AvlTreeRangeMapTest {

	@Test
	public void testInsert() {
		final RangeMap<Integer, String> map = new AVLTreeRangeMap<Integer, String>();
		map.insert(Range.create(0, 100), "0,100");
		map.insert(Range.create(-100, 0), "-100,-1");
		map.insert(Range.create(200, 300), "200,300");
		map.insert(Range.create(100, 200), "100,200");
		assertEquals("Not all ranges has been put", 4, map.size());
	}

	private void assertRange(final String expectedValue, final Range<Integer> expectedRange, Optional<String> acutal) {
		assertEquals("Wrong value", expectedValue, acutal.value());
		assertEquals("Wrong range", expectedRange, acutal.range());
	}

	@Test
	public void testFind() {
		final RangeMap<Integer, String> map = new AVLTreeRangeMap<Integer, String>();
		map.insert(Range.create(0, 100), "0,100");
		map.insert(Range.create(-100, 0), "-100,-1");
		map.insert(Range.create(200, 300), "200,300");
		map.insert(Range.create(100, 200), "100,200");
		assertRange("-100,-1", Range.create(-100, 0), map.find(-1));
		assertRange("0,100", Range.create(0, 100), map.find(0));
		assertRange("0,100", Range.create(0, 100), map.find(1));
		assertRange("0,100", Range.create(0, 100), map.find(77));
		assertRange("200,300", Range.create(200, 300), map.find(250));
		assertRange("100,200", Range.create(100, 200), map.find(150));
	}

	@Test
	public void testIsEmpty() {
		final RangeMap<Integer, String> map = new AVLTreeRangeMap<Integer, String>();
		assertTrue("Range Map should be empty", map.isEmpty());
		assertEquals("Range Map should be empty", 0, map.size());
	}

	@Test
	public void testClear() {
		final RangeMap<Integer, String> map = new AVLTreeRangeMap<Integer, String>();
		map.insert(Range.create(0, 100), "0,100");
		map.insert(Range.create(-100, 0), "-100,-1");
		map.insert(Range.create(200, 300), "200,300");
		map.insert(Range.create(100, 200), "100,200");
		map.clear();
		assertTrue("Range Map should be empty", map.isEmpty());
		assertEquals("Range Map should be empty", 0, map.size());

	}

	@Test
	public void testRemove() {
		final RangeMap<Integer, String> map = new AVLTreeRangeMap<Integer, String>();
		map.insert(Range.create(0, 100), "0,100");
		map.insert(Range.create(-100, 0), "-100,-1");
		map.insert(Range.create(200, 300), "200,300");
		map.insert(Range.create(100, 200), "100,200");
		// remove root
		assertEquals("Wrong removed", "0,100", map.remove(Range.create(0, 100)));
		assertFalse("The key is removed but get stil return value", map.find(1).isPresent());
		assertEquals("Removing currups tree", "100,200", map.find(150).value());
		assertEquals("Removing currups tree", "200,300", map.find(250).value());
		assertEquals("Removing currups tree", "-100,-1", map.find(-77).value());
		assertEquals("Removing currups tree", 3, map.size());
		// remove an leaf
		assertEquals("Wrong removed", "200,300", map.remove(Range.create(200, 300)));
		assertFalse("The key is removed but get stil return value", map.find(250).isPresent());
		assertEquals("Removing currups tree", "100,200", map.find(150).value());
		assertEquals("Removing currups tree", "-100,-1", map.find(-77).value());
		assertEquals("Removing currups tree", 2, map.size());
	}

}

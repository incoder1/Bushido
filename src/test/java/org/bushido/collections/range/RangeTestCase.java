package org.bushido.collections.range;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RangeTestCase {

	@Test
	public void shouldComareTo() {
		assertEquals("Must be greeter", 1, Range.create(0, 1).compareTo(Range.create(-1, 0)));
		assertEquals("Must be less", -1, Range.create(-1, 0).compareTo(Range.create(1, 100)));
		assertEquals("Must be equal", 0, Range.create(-100, 100).compareTo(Range.create(-50, 50)));
		assertEquals("Must be equal", Range.create(0, 100), Range.create(0, 100));
	}
	
	public void shouldEquals() throws Exception {
		assertEquals("Same ranages must be equal", Range.create(0, 100), Range.create(0, 100));
		assertFalse("Different by 0 comparable ranges must not equal", Range.create(-100, 100).equals(Range.create(-50, 50)));
	}

	@Test
	public void shouldContens() throws Exception {
		Range<Integer> tooH = Range.create(-100, 100);
		Range<Integer> oneH = Range.create(-50, 50);
		assertTrue("Must contain", tooH.contains(oneH));
	}
	
	@Test
	public void shouldNotContent() throws Exception {
		Range<Integer> l = Range.create(-100, 0);
		Range<Integer> g = Range.create(0, 100);
		assertFalse(l.contains(g));
		assertTrue(l.compareTo(g) < 0);
		assertTrue(g.compareTo(l) > 0);
	}
	
	@Test
	public void shouldCompareToKey() throws Exception {
		Range<Integer> r = Range.create(0, 100);
		assertTrue(0 == r.compareToKey(50));
		assertTrue(r.compareToKey(200) > 0 );
		assertTrue(r.compareToKey(-1) < 0);
	}

}

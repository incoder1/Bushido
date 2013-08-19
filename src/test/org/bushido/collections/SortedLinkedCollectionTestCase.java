package org.bushido.collections;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Collection;

import org.bushido.collections.skeeplist.SkeepList;
import org.junit.Test;

public class SortedLinkedCollectionTestCase {

	private static final Integer UNSORTED_DATA[] = { 9, 1, 7, 3, 5, 4, 6, 7, 2, 8,
			7, 0 };

//	@Test
//	public void testAutoSort() throws Exception {
//		final Collection<Integer> sortedList = new SkeepList<Integer>();
//		for (Integer integer : UNSORTED_DATA) {
//			sortedList.add(integer);
//		}
//		final Integer expected[] = UNSORTED_DATA.clone();
//		Arrays.sort(expected);
//		assertArrayEquals("List is not sorted", expected, sortedList.toArray());
//	}

}

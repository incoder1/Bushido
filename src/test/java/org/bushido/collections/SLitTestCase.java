package org.bushido.collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class SLitTestCase {

	@Test
	public void shouldAddAllAndCopyConstructorWork() throws Exception {
		final List<Integer> expected =  Arrays.asList(0,1,2,3,4,5,6,7);
		final List<Integer> sList = new SList<Integer>(expected);
		final Iterator<Integer> expectedIt = expected.iterator();
		final Iterator<Integer> slistIt = sList.iterator();
		while(slistIt.hasNext()) {
			assertEquals("Not proper order",expectedIt.next(),slistIt.next());
		}
		assertFalse("Lists are differ",expectedIt.hasNext());
	}
	
	@Test
	public void shouldGetByIndex() throws Exception {
		final List<Integer> sList = new SList<Integer>(Arrays.asList(0,1,2,3,4,5,6,7));
		for(int i=0; i <8; i++) {
			assertEquals("Get return not correct value", Integer.valueOf(i), sList.get(i));
		}
	}
	
	@Test
	public void shouldInsertInside() throws Exception {
		final List<Integer> sList = new SList<Integer>(Arrays.asList(0,1,2,4,5,6,7));
		sList.add(3, Integer.valueOf(3));
		for(int i=0; i <8; i++) {
			assertEquals("Get return not correct value", Integer.valueOf(i), sList.get(i));
		}
	}
	
	@Test
	public void shoudlRemoveByIndex() throws Exception {
		final List<Integer> sList = new SList<Integer>(Arrays.asList(0,1,2,3,4,5,6,7));
		sList.remove(0);
		sList.remove(3);
		sList.remove(5);
		final Integer[] expetect = {1,2,3,5,6};
		assertArrayEquals("Incorect removal by index",expetect, sList.toArray());
	}
	
	@Test
	public void shoudlRemoveByValue() throws Exception {
		final List<Integer> sList = new SList<Integer>(Arrays.asList(0,1,2,3,4,5,6,7));
		sList.remove(Integer.valueOf(0));
		sList.remove(Integer.valueOf(4));
		sList.remove(Integer.valueOf(7));
		final Integer[] expetect = {1,2,3,5,6};
		assertArrayEquals("Incorect removal by object value",expetect, sList.toArray());
	}

}

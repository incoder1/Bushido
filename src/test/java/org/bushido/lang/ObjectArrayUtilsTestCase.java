package org.bushido.lang;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class ObjectArrayUtilsTestCase {

	public static <T> int findNotNullSequence(int startIndex, T[] array) {
		int result = 0;
		for (int i = startIndex; (i < array.length) && (null != array[i]); i++) {
			++result;
		}
		return result;
	}

	@Test
	public void testCompact() {
		final Byte src[] = { null, 0, 1, 2, null, 3, null, null, 4, 5, 6, null,
				null, null, 7, null };
		ObjectArrayUtils.print(src);
		System.out.println(" " + src.length);
		final Byte expected[] = { 0, 1, 2, 3, 4, 5, 6, 7, null, null, null,
				null, null, null, null, null };
		ObjectArrayUtils.compact(src);
		ObjectArrayUtils.print(src);
		System.out.println("expected");
		ObjectArrayUtils.print(expected);
		System.out.println(" " + expected.length);

		assertTrue(Arrays.equals(expected, src));
	}

	@Test
	// spend less then 400 ms on JVM Windows x64 Intel Core I7 Oracle HotSpot
	// 64bit JVM
	// the size is 124327808 elements
	public void testCompactPerformance() {
		// int size = (int)Runtime.getRuntime().freeMemory();
		int size = 300_000_000;
		final Byte arr[] = new Byte[size];
		for (int i = 0; i < arr.length; i += (arr.length / 4)) {
			for (int j = i; j < i + (arr.length / 4); j++) {
				arr[j] = Byte.MAX_VALUE;
			}
			i += (arr.length / 4);
		}
		Compiler.compileClass(ObjectArrayUtils.class);
		ObjectArrayUtils.compact(arr);
		assertEquals("Not compacted", (arr.length / 2),
				findNotNullSequence(0, arr));
	}

}

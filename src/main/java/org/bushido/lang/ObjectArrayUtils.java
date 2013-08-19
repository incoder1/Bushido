package org.bushido.lang;

import java.io.PrintStream;

/**
 * Utility class for common object array operation
 * 
 * @author Victor_Gubin
 * 
 */
public final class ObjectArrayUtils {
	private ObjectArrayUtils() {
	}

	/**
	 * Compacting (defrag) array of object I.e. move all not {@code null}
	 * elements into beginning of array.
	 * 
	 * Algorithm has O(N) complexity
	 * 
	 * @param array
	 *            objects array instance to compact
	 */
	public static <T> void compact(final T[] array) {
		if (null != array && array.length > 0) {
			int index = notEmptyLength(0, array);
			int offset = 0;
			int moveBlockSize = 0;
			while (index <= array.length - 1) {
				offset += findNullSequence((index + offset + moveBlockSize),
						array);
				index += moveBlockSize;
				moveBlockSize = notEmptyLength(index + offset, array);
				// done
				if (0 == moveBlockSize) {
					break;
				}
				// no way to improve, System.arraycopy using additional memory
				// for movable block
				swap(array, index, offset, moveBlockSize);
			}
		}
	}

	private static <T> void swap(final T[] array, int start, final int offseet,
			int size) {
		for (int i = start; i < start + size; i++) {
			array[i] = array[i + offseet];
			array[i + offseet] = null;
		}
	}

	/**
	 * Prints {@code array} into {@link PrintStream} using
	 * {@link Object#toString()} method for elements
	 * 
	 * @param array
	 *            objects array instance to print
	 * @param ps
	 *            a print stream to print
	 */
	public static <T> void print(final T[] array, final PrintStream ps) {
		ps.print('[');
		if (null != array) {
			for (int i = 0; i < array.length - 1; i++) {
				ps.print(array[i]);
				ps.print(',');
			}
		}
		ps.print(array[array.length - 1]);
		ps.print(']');
		ps.println();
		ps.flush();
	}

	/**
	 * Prints array {@code array} into system output stream
	 * 
	 * @param array
	 *            objects array instance to print
	 */
	public static <T> void print(final T array[]) {
		print(array, System.out);
	}

	/*
	 * continues not empty block
	 */
	private static <T> int notEmptyLength(int startIndex, T[] array) {
		int result = 0;
		for (int i = startIndex; (i < array.length) && (null != array[i]); i++) {
			++result;
		}
		return result;
	}

	/*
	 * Continues empty block
	 */
	private static <T> int findNullSequence(int startIndex, T[] array) {
		int result = 0;
		for (int i = startIndex; (i < array.length) && (null == array[i]); i++) {
			++result;
		}
		return result;
	}

}

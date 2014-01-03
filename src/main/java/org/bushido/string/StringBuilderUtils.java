/*
   This library is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library.  If not, see <http://www.gnu.org/licenses/>. 
 */
package org.bushido.string;

/**
 * Utility class for manipulating {@link StringBuilder} like {@link String}
 * 
 * @author Victor Gubin
 * 
 */
public final class StringBuilderUtils {

	/**
	 * Replaces all occurrences of <code>oldChar</code> in this string with
	 * <code>newChar</code>.
	 * 
	 * @param src
	 *            source {@link StringBuilder} to be modified
	 * @param oldChar
	 *            the old character.
	 * @param newChar
	 *            newChar the new character
	 * @see String#replace(char, char)
	 */
	public static void replace(final StringBuilder src, char oldChar,
			char newChar) {
		for (int i = 0; i < src.length(); i++) {
			if (src.charAt(i) == oldChar) {
				src.setCharAt(i, newChar);
			}
		}
	}

	/**
	 * Omits leading and trailing whitespace of the {@code src}
	 * 
	 * @param src
	 *            source {@link StringBuilder} to be modified
	 * @see String#trim()
	 */
	public static void trim(final StringBuilder src) {
		int i = src.length() - 1;
		while (i > 0 && Character.isWhitespace(src.charAt(i))) {
			--i;
		}
		src.delete(i + 1, src.length());
		i = 0;
		while ((i < src.length()) && Character.isWhitespace(src.charAt(i))) {
			++i;
		}
		src.delete(0, i);
	}

	/**
	 * Converts all of the characters in {@code src} to to upper case using the
	 * rules of the default locale.
	 * 
	 * @param src
	 *            source {@link StringBuilder} to be modified
	 * @see String#toUpperCase()
	 */
	public static void toUpperCase(final StringBuilder src) {
		for (int i = 0; i < src.length(); i++) {
			src.setCharAt(i, Character.toUpperCase(src.charAt(i)));
		}
	}

	/**
	 * Converts all of the characters in {@code src} to to lower case using the
	 * rules of the default locale.
	 * 
	 * @param src
	 *            source {@link StringBuilder} to be modified
	 * @see String#toLowerCase()
	 */
	public static void toLowerCase(final StringBuilder src) {
		for (int i = 0; i < src.length(); i++) {
			src.setCharAt(i, Character.toLowerCase(src.charAt(i)));
		}
	}

	/**
	 * Removes all whitespace characters from the string buffer
	 * 
	 * @param src
	 *            source {@link StringBuilder} to be modified
	 */
	public static void minimize(final StringBuilder src) {
		boolean hasWhiteSpaces = true;
		while (hasWhiteSpaces) {
			int start;
			for (start = 0; start < src.length()
					&& (!Character.isWhitespace(src.charAt(start))); start++) {
			}
			hasWhiteSpaces = start != src.length();
			if (hasWhiteSpaces) {
				int end;
				for (end = start; end < src.length()
						&& Character.isWhitespace(src.charAt(end)); end++)
					;
				src.delete(start, end);
			}
		}
	}

	/*
	 * Avoid this class instance creation
	 */
	private StringBuilderUtils() {
	}
}

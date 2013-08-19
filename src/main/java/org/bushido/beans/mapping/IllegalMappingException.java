/*
 *  This library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library.  If not, see <http://www.gnu.org/licenses/>. 
 */
package org.bushido.beans.mapping;

/**
 * Exception of this type occurs when method mapping are illegal
 * 
 * @author Victor Gubin
 */
public class IllegalMappingException extends RuntimeException {
	/**
	 * Constructs new {@code IllegalMappingException}
	 * 
	 * @param src
	 *            source mapping bean class
	 * @param dest
	 *            destination mapping bean class
	 */
	public IllegalMappingException(final Class<?> src, final Class<?> dest) {
		super(src.getCanonicalName() + " has no mappings to "
				+ dest.getCanonicalName());
	}

	/**
	 * {@inheritDoc}
	 */
	public IllegalMappingException(final String msg) {
		super(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	public IllegalMappingException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 6950198308129201373L;
}

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
package org.bushido.beans.convertor;

/**
 * Implementation of {@code CustomConverter} is used for custom type cast
 * (convert) between {@code SrcType} and {@code DestType}
 * 
 * @author Victor Gubin
 * 
 * @param <SrcType>
 *            source type
 * @param <DestType>
 *            destination type
 */
public interface CustomConverter<SrcType, DestType> {
	/**
	 * Convert (cast) {@code dest} from {@code SrcType} to the {@code DestType}
	 * 
	 * @param src
	 *            {@code SrcType} object instance
	 * @return converted object instance
	 */
	public DestType convert(final SrcType src);
}

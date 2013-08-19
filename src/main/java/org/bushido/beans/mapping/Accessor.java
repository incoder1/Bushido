/*
 *  This library is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library.  If not, see <http://www.gnu.org/licenses/>. 
 */
package org.bushido.beans.mapping;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Accessor<TMetod> {
	private final TMetod method;
	private final int index;

	public Accessor(TMetod method, int index) {
		this.method = method;
		this.index = index;
	}

	public TMetod getMethod() {
		return method;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(method).append(index).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = this == obj;
		if (!result) {
			result = (null != obj) && (obj instanceof Accessor);
			if (result) {
				if (((Accessor<?>) obj).getMethod().getClass() == method
						.getClass()) {
					@SuppressWarnings("unchecked")
					final Accessor<TMetod> oth = (Accessor<TMetod>) obj;
					result = new EqualsBuilder()
							.append(this.method, oth.method)
							.append(this.index, oth.index).isEquals();
				} else {
					result = false;
				}
			}
		}
		return result;
	}

}

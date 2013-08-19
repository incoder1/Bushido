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

import net.sf.cglib.reflect.FastMethod;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public final class CallPair {
	private final FastMethod method;
	private final Accessor<FastMethod>[] callstack;

	public CallPair(FastMethod method, Accessor<FastMethod>[] callstack) {
		this.method = method;
		this.callstack = callstack;
	}

	public FastMethod getMethod() {
		return method;
	}

	public Accessor<FastMethod>[] getCallstack() {
		return callstack;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(method).append(callstack)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = this == obj;
		if (!result) {
			result = (null != obj) && (obj instanceof CallPair);
			if (result) {
				final CallPair oth = (CallPair) obj;
				result = new EqualsBuilder().append(this.method, oth.method)
						.append(this.callstack, oth.callstack).isEquals();
			}
		}
		return result;
	}

}
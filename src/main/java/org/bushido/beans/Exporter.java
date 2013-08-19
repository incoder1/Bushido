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
package org.bushido.beans;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.reflect.FastMethod;

import org.bushido.beans.convertor.CustomConverter;
import org.bushido.beans.mapping.Accessor;
import org.bushido.beans.mapping.CallPair;
import org.bushido.beans.mapping.IllegalMappingException;
import org.bushido.beans.mapping.Mapping;

/**
 * Exports (injects) data obtained from the source bean to the destination bean
 * 
 * @author Victor Gubin
 * 
 * @param <Src>
 *            source bean type
 * @param <Dest>
 *            destination bean type
 */
final class Exporter<Src, Dest> implements Injector<Src, Dest> {

	private final Mapping mapping;

	private final Map<Method, Object> cache;

	Exporter(final Mapping mapping) {
		this.mapping = mapping;
		this.cache = new HashMap<Method, Object>();
	}

	private final void invokeSequence(final Dest dest,
			final Accessor<FastMethod>[] sequence, final Object[] value)
			throws InvocationTargetException {
		final Accessor<FastMethod> setter = sequence[sequence.length - 1];
		Object instance = cache.get(setter.getMethod().getJavaMethod());
		if (null == instance) {
			instance = dest;
			for (int i = 0; i < sequence.length - 1; i++) {
				FastMethod method = sequence[i].getMethod();
				instance = method.invoke(instance, null);
				if (sequence[i].getIndex() >= 0) {
					instance = solveIndex(instance, sequence[i].getIndex());
				}
			}
			cache.put(setter.getMethod().getJavaMethod(), instance);
		}
		setter.getMethod().invoke(instance, value);
	}

	@SuppressWarnings("unchecked")
	private Object solveIndex(final Object vector, int index) {
		if (vector.getClass().isArray()) {
			return Array.get(vector, index);
		} else if (vector instanceof List<?>) {
			return ((List<Object>) vector).get(index);
		} else {
			throw new IllegalMappingException(
					"Resulting value can be only array or List type");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bushido.beans.Injector#inject(Src, Dest)
	 */
	@Override
	public void inject(final Src src, final Dest dest) {
		final Collection<CallPair> mapping = this.mapping.getMapping();
		final Map<Method, CustomConverter<?, ?>> converters = this.mapping
				.getConverterMapping();
		if (mapping.isEmpty()) {
			throw new IllegalStateException("No mapping for classes found");
		}
		final Object params[] = new Object[1];
		try {
			for (CallPair it : mapping) {
				params[0] = it.getMethod().invoke(src, null);
				@SuppressWarnings("unchecked")
				final CustomConverter<Object, Object> converter = (CustomConverter<Object, Object>) converters
						.get(it.getMethod().getJavaMethod());
				if (null != converter) {
					params[0] = converter.convert(params[0]);
				}
				invokeSequence(dest, it.getCallstack(), params);
			}
		} catch (InvocationTargetException exc) {
			throw new IllegalStateException(exc);
		}
	}

}

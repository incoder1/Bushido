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
 * Imports (injects) data obtained from the source bean to the destination bean
 * 
 * @author Victor Gubin
 * 
 * @param <Src>
 *            source bean type
 * @param <Dest>
 *            destination bean type
 */
final class Importer<Src, Dest> implements Injector<Src, Dest> {

	private final Mapping mapping;

	private final Map<Method, Object> cache;

	Importer(final Mapping mapping) {
		this.mapping = mapping;
		this.cache = new HashMap<Method, Object>();
	}

	private final Object invokeSequence(final Src src,
			final Accessor<FastMethod>[] sequence)
			throws InvocationTargetException {
		FastMethod lastGetter = sequence[sequence.length - 1].getMethod();
		Object instance = cache.get(lastGetter.getJavaMethod());
		if (null == instance) {
			instance = src;
			for (int i = 0; i < sequence.length - 1; i++) {
				FastMethod method = sequence[i].getMethod();
				instance = method.invoke(instance, null);
				if (sequence[i].getIndex() >= 0) {
					instance = solveIndex(instance, sequence[i].getIndex());
				}
			}
			cache.put(lastGetter.getJavaMethod(), instance);
		}
		Object result = lastGetter.invoke(instance, null);
		if (sequence[sequence.length - 1].getIndex() >= 0) {
			result = solveIndex(result,
					sequence[sequence.length - 1].getIndex());
		}
		return result;
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
		final Map<Method, CustomConverter<?, ?>> conveteres = this.mapping
				.getConverterMapping();
		if (mapping.isEmpty()) {
			throw new IllegalStateException("No mapping for classes");
		}
		final Object params[] = new Object[1];
		try {
			for (CallPair it : mapping) {
				params[0] = this.invokeSequence(src, it.getCallstack());
				@SuppressWarnings("unchecked")
				final CustomConverter<Object, Object> converter = (CustomConverter<Object, Object>) conveteres
						.get(it.getMethod().getJavaMethod());
				if (null != converter) {
					params[0] = converter.convert(params[0]);
				}
				it.getMethod().invoke(dest, params);
			}
		} catch (InvocationTargetException exc) {
			throw new IllegalStateException(exc);
		}
	}
}

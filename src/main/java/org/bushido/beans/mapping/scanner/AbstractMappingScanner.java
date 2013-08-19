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
package org.bushido.beans.mapping.scanner;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.bushido.beans.convertor.CustomConverter;
import org.bushido.beans.mapping.Accessor;
import org.bushido.beans.mapping.CallPair;
import org.bushido.beans.mapping.Converter;
import org.bushido.beans.mapping.IllegalMappingException;

abstract class AbstractMappingScanner implements MappingScanner {

	private final List<CallPair> methodMapping;
	private final Map<Method, CustomConverter<?, ?>> convetersMapping;

	public AbstractMappingScanner() {
		this.methodMapping = new LinkedList<CallPair>();
		this.convetersMapping = new HashMap<Method, CustomConverter<?, ?>>();
	}

	protected void validateSetter(final Method setter) {
		if (setter.getParameterTypes().length != 1) {
			throw new IllegalMappingException(setter.getName()
					+ " shoud have one parrameter");
		}
	}

	protected boolean checkVoidReturnType(final Method method) {
		return method.getReturnType().equals(Void.TYPE);
	}

	protected Accessor<FastMethod>[] fastCallStack(FastClass root,
			final Accessor<Method>[] callstack) {
		@SuppressWarnings("unchecked")
		final Accessor<FastMethod>[] result = new Accessor[callstack.length];
		FastClass clazz = root;
		for (int i = 0; i < callstack.length - 1; i++) {
			final FastMethod fastMethod = clazz.getMethod(callstack[i]
					.getMethod());
			result[i] = new Accessor<FastMethod>(fastMethod,
					callstack[i].getIndex());
			clazz = FastClass.create(fastMethod.getReturnType());
			Compiler.compileClass(clazz.getClass());
		}
		final FastMethod fastMethod = clazz
				.getMethod(callstack[callstack.length - 1].getMethod());
		result[callstack.length - 1] = new Accessor<FastMethod>(
				fastMethod, callstack[callstack.length - 1].getIndex());
		return result;

	}

	protected final CustomConverter<?, ?> scanConvertors(final Method it) {
		final Converter convertor = it.getAnnotation(Converter.class);
		if (convertor != null) {
			if (null == convertor.value()) {
				throw new IllegalMappingException(
						"null is illegal convertor type");
			}
			final Set<Class<?>> interfaces = new HashSet<Class<?>>(
					Arrays.asList(convertor.value().getInterfaces()));
			if (!interfaces.contains(CustomConverter.class)) {
				throw new IllegalMappingException(convertor.value().getName()
						+ " should implement TypeConvertor");
			}
			try {
				return convertor.value().newInstance();
			} catch (InstantiationException e) {
				throw new IllegalMappingException(
						"Conveter must have public visibility and default public constructor",
						e);
			} catch (IllegalAccessException e) {
				throw new IllegalMappingException(
						"Conveter must have  public visibility and default public constructor",
						e);
			}
		}
		return null;
	}

	protected final List<CallPair> getMethodMapping() {
		return methodMapping;
	}

	protected final Map<Method, CustomConverter<?, ?>> getConvetersMapping() {
		return convetersMapping;
	}
}
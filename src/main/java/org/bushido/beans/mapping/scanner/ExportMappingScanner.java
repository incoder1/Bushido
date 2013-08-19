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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.bushido.beans.convertor.CustomConverter;
import org.bushido.beans.mapping.Accessor;
import org.bushido.beans.mapping.CallPair;
import org.bushido.beans.mapping.Converter;
import org.bushido.beans.mapping.ExportFlow;
import org.bushido.beans.mapping.ExportSource;
import org.bushido.beans.mapping.IllegalMappingException;
import org.bushido.beans.mapping.Mapping;
import org.bushido.beans.mapping.Path;
import org.bushido.beans.mapping.Setter;
import org.bushido.beans.mapping.Transient;

class ExportMappingScanner extends AbstractMappingScanner {

	public ExportMappingScanner() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Mapping scan(Class<?> src, Class<?> dest) {
		if (!src.isAnnotationPresent(ExportSource.class)) {
			throw new IllegalMappingException(
					"No mapping fond from source bean" + src.getName()
							+ " to destination bean" + dest.getName());
		}
		final ExportSource source = src.getAnnotation(ExportSource.class);
		final FastClass fastDest = FastClass.create(dest);
		final FastClass fastSrc = FastClass.create(src);
		Compiler.compileClass(fastSrc.getClass());
		final Method srcMethods[] = src.getMethods();
		for (Method it : srcMethods) {
			if (it.getDeclaringClass() == Object.class) {
				continue;
			}
			// continue if no mapping annotation
			if (it.isAnnotationPresent(ExportFlow.class)) {
				final ExportFlow flow = it.getAnnotation(ExportFlow.class);
				final Path path = flow.path();
				final Setter setter = flow.value();
				Class<?> conveter = null;
				if (it.isAnnotationPresent(Converter.class)) {
					Converter conv = it.getAnnotation(Converter.class);
					conveter = conv.value();
				}
				final FastMethod fastGetter = fastSrc.getMethod(it);

				final List<Accessor<Method>> callStack = findCallStack(dest,
						it, path, setter, conveter);

				final Accessor<Method> callStackArr[] = new Accessor[callStack
						.size()];
				callStack.toArray(callStackArr);

				final Accessor<FastMethod>[] fastCallStack = fastCallStack(
						fastDest, callStackArr);
				getMethodMapping().add(new CallPair(fastGetter, fastCallStack));
				final CustomConverter<?, ?> conv = scanConvertors(it);
				if (null != conv) {
					getConvetersMapping().put(it, conv);
				}
			} else if (source.compliant()) {
				// ignore Transient setters
				if (null != it.getAnnotation(Transient.class)) {
					continue;
				}
				final Method setter = findCompliantSetter(it, dest);
				// just skip if method is not compliant
				if (setter == null) {
					continue;
				}
				final FastMethod fastGetter = fastSrc.getMethod(it);
				final Accessor<FastMethod>[] callStack = new Accessor[1];
				callStack[0] = new Accessor<FastMethod>(
						fastDest.getMethod(setter), -1);
				this.getMethodMapping()
						.add(new CallPair(fastGetter, callStack));
				// check for the converter
				final CustomConverter<?, ?> conv = scanConvertors(it);
				if (null != conv) {
					getConvetersMapping().put(it, conv);
				}
			}
		}
		return new Mapping(getMethodMapping(), getConvetersMapping());
	}

	private List<Accessor<Method>> findCallStack(Class<?> dest, Method it,
			final Path path, final Setter setter, Class<?> conveter) {
		List<Accessor<Method>> callStack = null;
		if (path.value().length() > 0) {
			callStack = findObtainCallStack(it, dest, path.value());
			final Method lastSetter = getLastSetter(it, callStack,
					setter.value());
			callStack.add(new Accessor<Method>(lastSetter, -1));
		} else {
			callStack = getSimpleSetter(dest, it, setter.value(), conveter);
		}
		return callStack;
	}

	private List<Accessor<Method>> getSimpleSetter(final Class<?> dest,
			final Method getter, final String name, final Class<?> converter) {
		@SuppressWarnings("unchecked")
		final Accessor<Method> result[] = new Accessor[1];

		Class<?> returnType = null;
		if (null == converter) {
			returnType = getter.getReturnType();
		} else {
			for (Method method : converter.getMethods()) {
				if (method.getDeclaringClass() == Object.class) {
					continue;
				}
				if (method.getName().equals("convert")) {
					returnType = method.getReturnType();
					break;
				}
			}
		}
		if (returnType == Void.class) {
			throw new IllegalMappingException(getter.getName()
					+ " should return a value");
		}
		try {
			result[0] = new Accessor<Method>(dest.getMethod(name, returnType),
					-1);
		} catch (final SecurityException e) {
			throw new IllegalMappingException("Can't access to the getter", e);
		} catch (final NoSuchMethodException e) {
			// handle auto boxing
			final Method[] methods = dest.getMethods();
			for (Method it : methods) {
				if (it.getName().equals(name)
						&& (it.getParameterTypes().length == 1)
						&& it.getParameterTypes()[0].isPrimitive()) {
					result[0] = new Accessor<Method>(it, -1);
				}
			}
			if (null == result[0]) {
				throw new IllegalMappingException(
						"Can't obtain setter, no such method", e);
			}
		}
		return new LinkedList<Accessor<Method>>(Arrays.asList(result));
	}

	private Method getLastSetter(final Method getter,
			final List<Accessor<Method>> obtainStack, final String name) {
		final Class<?> nestingClass = obtainStack.get(obtainStack.size() - 1)
				.getMethod().getReturnType();
		final Class<?> resultType = getter.getReturnType();
		if (resultType == Void.class) {
			throw new IllegalMappingException(getter.getName()
					+ " should return a value");
		}
		Method result;
		try {
			result = nestingClass.getMethod(name, resultType);
		} catch (SecurityException e) {
			throw new IllegalMappingException("Can't access to the getter", e);
		} catch (NoSuchMethodException e) {
			throw new IllegalMappingException("Can't obtain getter", e);
		}
		return result;
	}

	private List<Accessor<Method>> findObtainCallStack(final Method getter,
			final Class<?> dest, final String path) {
		if (null == path || path.length() == 0) {
			throw new IllegalMappingException("Wrong mapping configuration of "
					+ getter.getName());
		}
		List<Accessor<Method>> result = null;
		try {
			Accessor<Method>[] obtainPath = PathParser.parseMethodsPath(dest,
					path);
			result = new ArrayList<Accessor<Method>>(Arrays.asList(obtainPath));
		} catch (SecurityException e) {
			new IllegalStateException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalMappingException("Path is invalid", e);
		}
		final Method finalGetter = result.get(result.size() - 1).getMethod();
		if (checkVoidReturnType(finalGetter)) {
			throw new IllegalMappingException(finalGetter.getName()
					+ " must not be void function");
		}
		return result;
	}

	private Method findCompliantSetter(final Method getter, final Class<?> dest) {
		String signature = getter.getName();
		if (signature.startsWith("is")) {
			signature = signature.substring(2);
		} else if (signature.startsWith("get")) {
			signature = signature.substring(3);
		} else {
			return null;
		}
		final String setSignature = "set" + signature;
		Method setter = null;
		for (Method method : dest.getMethods()) {
			if (method.getDeclaringClass() == Object.class) {
				continue;
			}
			final String mName = method.getName();
			if (mName.equals(setSignature)) {
				setter = method;
				break;
			}
		}
		return setter;
	}

}

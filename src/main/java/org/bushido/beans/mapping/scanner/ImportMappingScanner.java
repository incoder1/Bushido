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

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.bushido.beans.convertor.CustomConverter;
import org.bushido.beans.mapping.Accessor;
import org.bushido.beans.mapping.CallPair;
import org.bushido.beans.mapping.IllegalMappingException;
import org.bushido.beans.mapping.ImportDestination;
import org.bushido.beans.mapping.Mapping;
import org.bushido.beans.mapping.Path;
import org.bushido.beans.mapping.Transient;

final class ImportMappingScanner extends AbstractMappingScanner {

	public ImportMappingScanner() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bushido.beans.mapping.scanner.MappingScanner#scan(java.lang.Class,
	 * java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Mapping scan(final Class<?> src, final Class<?> dest) {
		final ImportDestination destination = dest
				.getAnnotation(ImportDestination.class);
		if (null == destination) {
			throw new IllegalMappingException(
					"No mapping fond from source bean" + src.getName()
							+ " to destination bean" + dest.getName());
		}
		final FastClass fastDest = FastClass.create(dest);
		Compiler.compileClass(fastDest.getClass());
		final FastClass fastSrc = FastClass.create(src);
		final Method destMethods[] = dest.getMethods();
		for (Method it : destMethods) {
			// do not scan Object methods
			if (it.getDeclaringClass() == Object.class) {
				continue;
			}

			// continue if no mapping annotation
			if (it.isAnnotationPresent(Path.class)) {
				final Path source = it.getAnnotation(Path.class);
				validateSetter(it);
				final Accessor<Method> callstack[] = findGetterSetterCallStack(
						it, src, source.value());
				final FastMethod setter = fastDest.getMethod(it);
				final Accessor<FastMethod> fastCallStack[] = fastCallStack(
						fastSrc, callstack);
				this.getMethodMapping()
						.add(new CallPair(setter, fastCallStack));
				final CustomConverter<?, ?> convertor = this.scanConvertors(it);
				if (null != convertor) {
					this.getConvetersMapping().put(it, convertor);
				}
				// auto resolve names if complaint method signatures
			} else if (this.complaintSetter(destination, it)) {
				// ignore Transient setters
				if (null != it.getAnnotation(Transient.class)) {
					continue;
				}
				final Method getter = this.findComplaintGetter(it, src);
				// just skip if method is not complaint
				if (getter == null) {
					continue;
				}
				final FastMethod setter = fastDest.getMethod(it);
				final Accessor<FastMethod>[] callstack = new Accessor[] { new Accessor<FastMethod>(
						fastSrc.getMethod(getter), -1) };
				final CallPair pair = new CallPair(setter, callstack);
				this.getMethodMapping().add(pair);
				// check for the converter
				final CustomConverter<?, ?> convertor = this.scanConvertors(it);
				if (null != convertor) {
					this.getConvetersMapping().put(it, convertor);
				}
			}
		}
		return new Mapping(this.getMethodMapping(), this.getConvetersMapping());
	}

	private boolean complaintSetter(final ImportDestination destination,
			final Method method) {
		return destination.compliant() && method.getName().startsWith("set")
				&& method.getParameterTypes().length == 1;
	}

	private Accessor<Method>[] findGetterSetterCallStack(final Method setter,
			final Class<?> src, final String path) {
		if (null == path || path.length() == 0) {
			throw new IllegalMappingException("Wrong mapping configuration of "
					+ setter.getName());
		}

		Accessor<Method> result[] = null;
		try {
			result = PathParser.parseMethodsPath(src, path);
		} catch (SecurityException e) {
			new IllegalStateException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalMappingException("Path is invalid", e);
		}
		final Method finalGetter = result[result.length - 1].getMethod();
		if (checkVoidReturnType(finalGetter)) {
			throw new IllegalMappingException(finalGetter.getName()
					+ " must not be void function");
		}
		return result;
	}

	private Method findComplaintGetter(final Method setter, final Class<?> src) {
		final String signature = setter.getName().substring(3);
		final String regSignature = "get" + signature;
		final String boolSignature = "is" + signature;
		Method getter = null;
		for (Method method : src.getMethods()) {
			final String mName = method.getName();
			if (mName.equals(regSignature) || mName.equals(boolSignature)) {
				getter = method;
				break;
			}
		}
		if (null != getter && this.checkVoidReturnType(getter)) {
			getter = null;
		}
		return getter;
	}

}

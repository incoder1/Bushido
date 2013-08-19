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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bushido.beans.mapping.Accessor;
import org.bushido.beans.mapping.IllegalMappingException;

/**
 * Parse paths string for reflect obtaining of source data
 * 
 * @author Victor Gubin
 * 
 */
public class PathParser {

	private static final Pattern INDEXABLE = Pattern.compile("^.+\\[\\d+\\]$");
	private static final Pattern NUMBER = Pattern.compile("\\d+");

	/**
	 * Obtains cglib FastMethod call queue in sequence used for obtaining source
	 * data
	 * 
	 * @param rootClass
	 *            the root class of hierarchy
	 * @param path
	 *            path of methods to obtain source data
	 * @return array of holders which contains cglib FastMethods to be invoked for obtaining source
	 *         data
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static Accessor<Method>[] parseMethodsPath(final Class<?> rootClass,
			final String path) throws SecurityException, NoSuchMethodException {
		try {
			final String[] names = path.split("\\.");
			if (null == names || names.length == 0) {
				throw new IllegalMappingException("Path " + path
						+ " has invalid format");
			}
			@SuppressWarnings("unchecked")
			final Accessor<Method>[] result = new Accessor[names.length];
			Class<?> clazz = rootClass;
			for (int i = 0; i < names.length - 1; i++) {
				final AtomicReference<String> methodName = new AtomicReference<String>(names[i]);
				final AtomicInteger index = new AtomicInteger(-1);
				parseIndexCall(methodName, index);
				final Method method = clazz.getMethod(methodName.get());
				if(index.get() >= 0 && !(method.getReturnType().isArray() || method.getReturnType() == List.class) ) {
					throw new IllegalMappingException("Can only map index from array of List");
				}
				result[i] = new Accessor<Method>(method, index.get());
				final Class<?> returnType = method.getReturnType();
				if (!returnType.isPrimitive()) {
					clazz = returnType;
				} else if (returnType.isPrimitive() && (i < names.length - 1)) {
					throw new IllegalMappingException(path
							+ " is an invalid mapping");
				}
			}
			final AtomicReference<String> methodName = new AtomicReference<String>(names[names.length - 1]);
			final AtomicInteger index = new AtomicInteger(-1);
			parseIndexCall(methodName, index);
			result[names.length - 1] = new Accessor<Method>(clazz.getMethod(methodName.get()), index.get());
			return result;
		} catch (NoSuchMethodError exc) {
			throw new IllegalMappingException(path + " is an invalid mapping");
		}
	}

	private static void parseIndexCall(final AtomicReference<String> name,
			final AtomicInteger index) {
		String methodName = name.get();
		final Matcher indexableMatch = INDEXABLE.matcher(methodName);
		boolean result = indexableMatch.matches();
		if (result) {
			methodName = methodName.replaceAll("\\[\\d+\\]", "");
			index.set(readIndex(name.get()));
		}
		name.set(methodName);
	}

	private static int readIndex(final String name) {
		final Matcher matcher = NUMBER.matcher(name);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group());
		} else {
			throw new IllegalMappingException("Ivalid index format");
		}
	}

}

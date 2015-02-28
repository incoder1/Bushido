package org.bushido.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class Validate {

	public static <E extends Exception> void isTrue(boolean condition, final Class<E> errClass, String messageFormat,
			Object... args) throws E {
		if (!condition) {
			throwException(errClass, String.format(messageFormat, args));
		}
	}

	public static void isTrue(boolean condition, final String messageFormat, Object... args)
			throws IllegalArgumentException {
		if (!condition) {
			throw new IllegalArgumentException(String.format(messageFormat, args));
		}
	}
	
	public static void notNull(final Object obj, final String messageFomat,Object ...args) {
		isTrue(null != obj, messageFomat, args);
	}
	
	public static <E extends Exception> void notNull(final Object obj, final Class<E> errClass, String messageFormat,
			Object... args) throws E {
		isTrue(null != obj, errClass, messageFormat, args);
	}

	private static <E extends Exception> void throwException(Class<E> clazz, String msg) throws E {
		Constructor<E> constructor = null;
		try {
			constructor = clazz.getConstructor(String.class);
			throw constructor.newInstance(msg);
		} catch (SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException exc) {
			throw new IllegalStateException("Logical error! Exception of type " + clazz.getName()
					+ " can not be instantiated. Need a construcor with the String argument", exc);
		}

	}

	private Validate() {
	}
}

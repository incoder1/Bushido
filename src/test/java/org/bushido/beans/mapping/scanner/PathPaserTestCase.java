package org.bushido.beans.mapping.scanner;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.bushido.beans.mapping.Accessor;
import org.bushido.beans.mapping.scanner.PathParser;
import org.junit.Test;

public class PathPaserTestCase {

	class Level2 {
		private final int intField = Integer.MAX_VALUE;
		private String string;

		public int getInt() {
			return this.intField;
		}

		public void setString(final String string) {
			this.string = string;
		}

		public String getString() {
			return string;
		}
	}

	class Level1 {
		private final Level2 level2 = new Level2();
		private final String strArr[] = {"Lorem ipsum"};

		public Level2 getLevel2() {
			return this.level2;
		}

		public String[] getStrArr() {
			return strArr;
		}
	}

	class Level0 {
		private final int intField = Integer.MIN_VALUE;

		private final Level1 level1 = new Level1();

		public Level1 getLevel1() {
			return this.level1;
		}

		public int getIntField() {
			return intField;
		}

	}

	@Test
	public void findRootElementTest() throws Exception {
		final String path = "getIntField";
		final Method expected = Level0.class.getMethod("getIntField");
		final Accessor<Method> actual = PathParser.parseMethodsPath(Level0.class, path)[0];
		assertEquals("Wrong method returned from path", expected, actual.getMethod());
		final Level0 instance = new Level0();
		assertEquals("Wrong data obtained from source", Integer.MIN_VALUE,
				actual.getMethod().invoke(instance));
	}

	@Test
	public void findImportCallStack() throws Exception {
		final String path = "getLevel1.getLevel2.getInt";
		final Accessor<Method> callQueue[] = PathParser.parseMethodsPath(Level0.class,
				path);
		Object instance = new Level0();
		for (int i = 0; i < callQueue.length - 1; i++) {
			Method method = callQueue[i].getMethod();
			instance = method.invoke(instance);
		}
		Method last = callQueue[callQueue.length - 1].getMethod();
		final Object res = last.invoke(instance);
		assertEquals("Wrong value obtaned", Integer.MAX_VALUE, res);
	}
	
	@Test
	public void solveIndexed() throws Exception {
		final String path = "getLevel1.getStrArr[0]";
		final Accessor<Method> callQueue[] = PathParser.parseMethodsPath(Level0.class,
				path);
		Object instance = new Level0();
		for (int i = 0; i < callQueue.length - 1; i++) {
			Method method = callQueue[i].getMethod();
			instance = method.invoke(instance);
		}
		Method last = callQueue[callQueue.length - 1].getMethod();
		final Object res = last.invoke(instance);
		assertEquals("Wrong value obtaned", "Lorem ipsum",((String[])res)[0]);
	}
}

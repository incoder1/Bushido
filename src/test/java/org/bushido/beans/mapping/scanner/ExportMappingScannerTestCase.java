package org.bushido.beans.mapping.scanner;

import static junit.framework.Assert.assertEquals;

import java.lang.reflect.Method;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.bushido.beans.StringToFloatConvertor;
import org.bushido.beans.mapping.Converter;
import org.bushido.beans.mapping.ExportFlow;
import org.bushido.beans.mapping.ExportSource;
import org.bushido.beans.mapping.Mapping;
import org.bushido.beans.mapping.Path;
import org.bushido.beans.mapping.Setter;
import org.junit.Test;

public class ExportMappingScannerTestCase {
	private static class Level1 {
		public class Level2 {
			private String nestingString;

			public String getNestingString() {
				return nestingString;
			}

			public void setNestingString(String value) {
				this.nestingString = value;
			}

		}

		private Level2 level2 = new Level2();

		public Level2 getLevel2() {
			return level2;
		}
	}

	private static class DestBean {
		private final Level1 level1 = new Level1();

		private boolean bool;

		private float floating;

		public Level1 getLevel1() {
			return level1;
		}

		public boolean isBool() {
			return bool;
		}

		public void setBool(boolean bool) {
			this.bool = bool;
		}

		public float getFloating() {
			return floating;
		}

		public void setFloating(float floating) {
			this.floating = floating;
		}
	}

	@ExportSource
	private static class SimpleSource {
		private final String string = "Lorem ipsum dolor...";

		@ExportFlow(path = @Path("getLevel1.getLevel2"), value = @Setter("setNestingString"))
		public String getString() {
			return string;
		}
	}

	@ExportSource(compliant = true)
	private static class CompliantSource extends SimpleSource {
		private final boolean bool = true;

		public boolean isBool() {
			return bool;
		}
	}

	@ExportSource
	public static class CoverterSource {
		private final String floatContentString = "127.0e+7";
		
		@ExportFlow(@Setter("setFloating"))
		@Converter(StringToFloatConvertor.class)
		public String getFloatContentString() {
			return floatContentString;
		}
	}

	@Test
	public void shouldParseNested() throws Exception {
		final Class<Level1.Level2> destClass = Level1.Level2.class;
		final Class<SimpleSource> srcClass = SimpleSource.class;
		final Method setter = destClass.getMethod("setNestingString",
				String.class);
		final FastClass fastLevel2 = FastClass.create(destClass);
		final FastMethod expectedSetter = fastLevel2.getMethod(setter);
		final FastMethod expectedGetter = FastClass.create(srcClass).getMethod(
				"getString", null);

		final ExportMappingScanner scanner = new ExportMappingScanner();
		final Mapping mapping = scanner.scan(SimpleSource.class,
				DestBean.class);
		
		//assertTrue("No source getter in mapping", mapping.getMapping().containsKey(expectedGetter));
		//assertEquals("Invalid mapping", 3,
		//		mapping.getMapping().get(expectedGetter).length);
		//assertEquals("Invalid mapping", expectedSetter, mapping.getMapping().get(expectedGetter)[2]);
	}

	@Test
	public void shouldSolveCompliant() throws Exception {
		final Class<CompliantSource> srcClass = CompliantSource.class;
		final Class<Level1.Level2> destClass = Level1.Level2.class;
		final Method setter = destClass.getMethod("setNestingString",
				String.class);
		final FastClass fastLevel2 = FastClass.create(destClass);
		final FastMethod expectedBoolSetter = fastLevel2.getMethod(setter);
		final FastMethod expectedBoolGetter = FastClass.create(srcClass)
				.getMethod("isBool", null);
		final FastMethod expectedStringGetter = FastClass.create(srcClass)
				.getMethod("getString", null);

		final ExportMappingScanner scanner = new ExportMappingScanner();
		final Mapping mapping = scanner.scan(CompliantSource.class,
				DestBean.class);
//		assertEquals("Ignored ExportFlow when compliant", expectedBoolSetter,
//				mapping.getMapping().c(expectedStringGetter)[2]);
//		assertTrue("No source getter in mapping", mapping.getMapping()
//				.containsKey(expectedBoolGetter));
	}
	
	@Test
	public void shouldSolveConvetor() throws Exception {
		final Class<CoverterSource> srcClass = CoverterSource.class;
		
		final ExportMappingScanner scanner = new ExportMappingScanner();
		final Mapping mapping = scanner.scan(CoverterSource.class,
				DestBean.class);
		assertEquals("No convetor found", 1, mapping.getConverterMapping().size());
	}

}

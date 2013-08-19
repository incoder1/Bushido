package org.bushido.beans;

import static org.junit.Assert.assertEquals;

import org.bushido.beans.mapping.Converter;
import org.bushido.beans.mapping.ImportDestination;
import org.bushido.beans.mapping.Path;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImporterTestCase {

	private static final Float FLOAT_RES = 70.07e+17f;

	private static class Level1 {
		class Level2 {
			private final String string = "Leveled string";
			private final String[] array = {"Lorem ipsum"};

			public String getString() {
				return string;
			}

			public String[] getArray() {
				return array;
			}
		}

		private Level2 level2 = new Level2();

		public Level2 getLevel2() {
			return level2;
		}
	}

	private static class SrcBean {
		private final int integer = 100;
		private final String string = "String";
		private final Level1 object = new Level1();
		private final String srcForFloat = "70.07E17f";

		public int getInteger() {
			return integer;
		}

		public Level1 getLevel1() {
			return object;
		}

		public String getString() {
			return string;
		}

		public String getSrcForFloat() {
			return srcForFloat;
		}
	}

	@ImportDestination
	private static class DestBean {
		private String string;
		private int integer;
		private String nestedString;
		private String nestedArrayString;
		private float floatFiled;

		public String getString() {
			return string;
		}

		@Path("getString")
		public void setString(String string) {
			this.string = string;
		}
		
		public int getInteger() {
			return integer;
		}
		
		@Path("getInteger")
		public void setInteger(int integer) {
			this.integer = integer;
		}

		public String getNestedString() {
			return nestedString;
		}

		@Path("getLevel1.getLevel2.getString")
		public void setNestedString(String leveledString) {
			this.nestedString = leveledString;
		}

		public float getFloatFiled() {
			return floatFiled;
		}

		@Path("getSrcForFloat")
		@Converter(StringToFloatConvertor.class)
		public void setFloatFiled(float floatFiled) {
			this.floatFiled = floatFiled;
		}

		public String getNestedArrayString() {
			return nestedArrayString;
		}
		
		@Path("getLevel1.getLevel2.getArray[0]")
		public void setNestedArrayString(String nestedArrayString) {
			this.nestedArrayString = nestedArrayString;
		}
	}

	private static Injector<SrcBean, DestBean> IMPORTER;

	@BeforeClass
	public static void setUp() throws Exception {
		IMPORTER = InjectorFactory.newFactory().createImporter(SrcBean.class,
				DestBean.class);
	}

	@Test
	public void shouldCorretlyImportData() throws Exception {
		final SrcBean srcBean = new SrcBean();
		final DestBean destBean = new DestBean();
		IMPORTER.inject(srcBean, destBean);
		assertEquals("Integer field mistmach", srcBean.getInteger(),
				destBean.getInteger());
		assertEquals("String field mistmach", srcBean.getString(),
				destBean.getString());
		assertEquals("Nesting leveled string mistmach", srcBean.getLevel1()
				.getLevel2().getString(), destBean.getNestedString());
		assertEquals("Custom converter not working", FLOAT_RES,
				Float.valueOf(destBean.getFloatFiled()));
	}

}

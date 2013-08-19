package org.bushido.beans;

import static junit.framework.Assert.assertEquals;

import org.bushido.beans.mapping.Converter;
import org.bushido.beans.mapping.ExportFlow;
import org.bushido.beans.mapping.ExportSource;
import org.bushido.beans.mapping.Path;
import org.bushido.beans.mapping.Setter;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExporterTestCase {

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
		private int integer;

		private boolean boolField;
		
		private float floatField;

		private final Level1 level1 = new Level1();

		public int getInteger() {
			return integer;
		}

		public void setInteger(int integer) {
			this.integer = integer;
		}

		public boolean isBoolField() {
			return boolField;
		}

		public void setBoolField(boolean boolField) {
			this.boolField = boolField;
		}
		
		public float getFloatField() {
			return floatField;
		}

		public void setFloatField(float floatField) {
			this.floatField = floatField;
		}

		public Level1 getLevel1() {
			return level1;
		}

	}

	@ExportSource
	private static class SrcBean {
		private final String string = "Lorem ipsum dolor...";
		private final int integer = Integer.MAX_VALUE;
		private final boolean bool = true;

		@ExportFlow(path = @Path("getLevel1.getLevel2"), value = @Setter("setNestingString"))
		public String getString() {
			return string;
		}

		@ExportFlow(@Setter("setInteger"))
		public int getInteger() {
			return integer;
		}

		@ExportFlow(@Setter("setBoolField"))
		public boolean isBool() {
			return bool;
		}
		@ExportFlow(@Setter("setFloatField"))
		@Converter(StringToFloatConvertor.class)
		public String getFloatString() {
			return "117.0e+5";
		}
	}

	private static Injector<SrcBean, DestBean> EXPORTER;

	@BeforeClass
	public static void setUp() throws Exception {
		EXPORTER = InjectorFactory.newFactory().createExporter(SrcBean.class,
				DestBean.class);
	}

	@Test
	public void shouldExportData() throws Exception {
		final SrcBean srcBean = new SrcBean();
		final DestBean destBean = new DestBean();
		EXPORTER.inject(srcBean, destBean);
		assertEquals("Wrong export of nestnig string", srcBean.getString(),
				destBean.getLevel1().getLevel2().getNestingString());
		assertEquals("Wrong export of intger field", srcBean.getInteger(),
				destBean.getInteger());
		assertEquals("Wrong export of boolean field", srcBean.isBool(),
				destBean.isBoolField());
		assertEquals("Wrong conveter logic", 117.0e+5f, destBean.getFloatField());
	}

}

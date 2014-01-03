package org.bushido.beans.mapping.scanner;

import static org.junit.Assert.assertEquals;

import org.bushido.beans.convertor.CustomConverter;
import org.bushido.beans.mapping.Converter;
import org.bushido.beans.mapping.IllegalMappingException;
import org.bushido.beans.mapping.ImportDestination;
import org.bushido.beans.mapping.Mapping;
import org.bushido.beans.mapping.Path;
import org.bushido.beans.mapping.Transient;
import org.junit.Test;

public class ImportMappingScannerTestCase {

	private static class WrongFormatDestination {
		public void getName() {
		}
	}

	@ImportDestination
	private static class RightDestination {
		private String name;
		private int code;

		public String getName() {
			return name;
		}

		@Path(value = "getName")
		public void setName(String name) {
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		@Path(value = "getCode")
		// check autoboxing
		public void setCode(final Integer code) {
			this.code = code;
		}
	}

	public static class StringToIntegerConverter implements
			CustomConverter<String, Integer> {
		@Override
		public Integer convert(String src) {
			return Integer.parseInt(src);
		}
	}

	private static final class RightSrc {
		public String getName() {
			return "name";
		}

		public String getCode() {
			return "1";
		}
	}

	@ImportDestination(compliant = true)
	private static final class RightCompilantDest {
		public void setName(final String sring) {
		}

		@Converter(value = StringToIntegerConverter.class)
		public void setCode(int code) {
		}

		@Transient
		public void setNothing() {
		}
	}

	@ImportDestination
	public static final class WrongDest {
		@Path(value = "getName")
		public int getNothing() {
			System.out.println("Do something");
			return 0;
		}
	}

	@Test(expected = IllegalMappingException.class)
	public void shouldFailWithWrongReturnType() throws Exception {
		new ImportMappingScanner().scan(WrongFormatDestination.class,
				RightDestination.class);
	}

	@Test
	public void shouldMapCompliant() throws Exception {
		final ImportMappingScanner scanner = new ImportMappingScanner();
		final Mapping mapping = scanner.scan(RightSrc.class,
				RightCompilantDest.class);
		assertEquals("2 methods must be present", 2, mapping.getMapping()
				.size());
		assertEquals("1 custom conveter must be present", 1, mapping
				.getConverterMapping().size());
	}

	@Test(expected = IllegalMappingException.class)
	public void shouldNotMapWrongSetters() throws Exception {
		final ImportMappingScanner scanner = new ImportMappingScanner();
		scanner.scan(RightSrc.class, WrongDest.class);
	}
}

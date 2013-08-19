package org.bushido.samples.beans;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.bushido.beans.Injector;
import org.bushido.beans.InjectorFactory;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

public class PerformanceTest {

	private static final int TEST_COUNT = 100000;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		directCallSpeedTest();
		bushidoSpeedTest();
		dozerSpeedTest();
	}

	private static void directCallSpeedTest() {
		final Level0 level0 = createNestedInstance();
		final InjectBean dest = new InjectBean();
		long startTime = System.currentTimeMillis();
		Level2 level2 = level0.getLevel1().getLevel2();
		for (int i = 0; i < TEST_COUNT; i++) {
			dest.setBoolField(level2.isBoolField());
			dest.setByteField(level2.getByteField());
			dest.setShortField(level2.getShortField());
			dest.setCharField(level2.getCharField());
			dest.setIntField(level2.getIntField());
			dest.setLongField(level2.getLongField());
			dest.setFloatField(level2.getFloatField());
			dest.setDoubleField(level2.getDoubleField());
			dest.setStringField(level2.getStringField());
			level2.setBoolField(dest.isBoolField());
			level2.setByteField(dest.getByteField());
			level2.setShortField(dest.getShortField());
			level2.setCharField(dest.getCharField());
			level2.setIntField(dest.getIntField());
			level2.setLongField(dest.getLongField());
			level2.setFloatField(dest.getFloatField());
			level2.setDoubleField(dest.getDoubleField());
			level2.setStringField(dest.getStringField());
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Direct import/export time is "
				+ (endTime - startTime) + " ms");
		System.gc();
	}

	private static void bushidoSpeedTest() {
		System.out.println("Bushido speed test");
		final InjectorFactory factory = InjectorFactory.newFactory();

		long startTime = System.currentTimeMillis();
		final Injector<Level0, InjectBean> importer = factory.createImporter(
				Level0.class, InjectBean.class);
		long endTime = System.currentTimeMillis();
		System.out.println("Importer mapping time is " + (endTime - startTime)
				+ " ms");
		System.gc();

		startTime = System.currentTimeMillis();
		final Injector<InjectBean, Level0> exporter = factory.createExporter(
				InjectBean.class, Level0.class);
		endTime = System.currentTimeMillis();
		System.out.println("Exporter mapping time is " + (endTime - startTime)
				+ " ms");
		System.gc();

		final Level0 level0 = createNestedInstance();

		final InjectBean dest = new InjectBean();

		startTime = System.currentTimeMillis();
		for (int i = 0; i < TEST_COUNT; i++) {
			exporter.inject(dest, level0);
			importer.inject(level0, dest);
		}
		endTime = System.currentTimeMillis();
		System.out.println("CGlib import/export time "
				+ (endTime - startTime) + " ms");

	}

	private static final void dozerSpeedTest() {
		long startTime = System.currentTimeMillis();
		final Mapper mapper = new DozerBeanMapper(
				Arrays.asList(new String[] { "dozer.xml" }));
		long endTime = System.currentTimeMillis();
		System.out.println("Dozer mapping time is " + (endTime - startTime)
				+ " ms");
		System.gc();
		final InjectBean dest = new InjectBean();
		final Level0 level0 = createNestedInstance();
		startTime = System.currentTimeMillis();
		for (int i = 0; i < TEST_COUNT; i++) {
			mapper.map(level0, dest);
			mapper.map(dest, level0);
		}
		endTime = System.currentTimeMillis();
		System.out.println("DOZER import/export time "
				+ TimeUnit.SECONDS.convert(endTime - startTime,
						TimeUnit.MILLISECONDS) + " s");
	}

	private static Level0 createNestedInstance() {
		final Level2 src = new Level2();
		src.setBoolField(true);
		src.setByteField(Byte.MAX_VALUE);
		src.setCharField('A');
		src.setShortField(Short.MAX_VALUE);
		src.setIntField(Integer.MAX_VALUE);
		src.setLongField(Long.MAX_VALUE);
		src.setFloatField(Float.MAX_VALUE);
		src.setDoubleField(Double.MAX_VALUE);
		src.setStringField("Lore ipsum");

		final Level1 level1 = new Level1();
		level1.setLevel2(src);

		final Level0 level0 = new Level0();
		level0.setLevel1(level1);
		return level0;
	}

}

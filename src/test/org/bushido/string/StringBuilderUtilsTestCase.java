package org.bushido.string;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Random;

import org.bushido.string.StringBuilderUtils;
import org.junit.Ignore;
import org.junit.Test;

public class StringBuilderUtilsTestCase {

	private static final String SRC = " AA bb    ccc Dddd EEE fff ooo fff gggg   ";
	private static final String TRIM_RES = SRC.trim();
	private static final String LOWER_CASE_RESULT = SRC.toLowerCase();
	private static final String UPPER_CASE_RESULT = SRC.toUpperCase();
	
	private static final int CHARACTER_COUNT = 1 << 10; // 1 Mb
	private static final int TEST_COUNT = 1 << 10;  
	
	@Test
	public void testTrim() throws Exception {
		final StringBuilder sb = new StringBuilder(SRC);		
		StringBuilderUtils.trim(sb);
		assertEquals("Invalid string structure", TRIM_RES, sb.toString());
	}
	
	@Test
	public void testUpperCase() throws Exception {
		final StringBuilder sb = new StringBuilder(SRC);
		StringBuilderUtils.toUpperCase(sb);
		assertEquals("Invalid string structure", UPPER_CASE_RESULT, sb.toString());
	}
	
	@Test
	public void testToLowerCase() throws Exception {
		final StringBuilder sb = new StringBuilder(SRC);
		StringBuilderUtils.toLowerCase(sb);
		assertEquals("Invalid string structure", LOWER_CASE_RESULT, sb.toString());
	}
	
	@Test
	public void testReplace() throws Exception {
		 final String expected = "!AA!bb!!!!ccc!Dddd!EEE!fff!ooo!fff!gggg!!!";
		 final StringBuilder result = new StringBuilder(SRC);
		 StringBuilderUtils.replace(result, ' ', '!');
		 assertEquals("Replace not correct", expected, result.toString());
	}
	
	@Test
	public void testMinimize() throws Exception {
		final String DEST = "AAbbcccDdddEEEfffooofffgggg";
		final StringBuilder result = new StringBuilder(SRC);
		StringBuilderUtils.minimize(result);
		assertEquals(DEST, result.toString());
	}
	
	private static final void newStringData(final int charCount,final StringBuilder dest) {
		dest.setLength(charCount);
		final Random rnd = new Random();
		int quarter = rnd.nextInt(charCount) / 4;
		// use Random chars to avoid jvm string cashing		
		for(int i=0; i < quarter ; i++) {
			dest.append(' ');
		}
		for(int i=0; i < quarter; i++) {
			rnd.setSeed(System.nanoTime());
			dest.append((char)rnd.nextInt(255));
		}
		for(int i=0; i < quarter; i++) {
			dest.append(' ');
		}
		for(int i=0; i < quarter; i++) {
			rnd.setSeed(System.nanoTime());
			dest.append((char)rnd.nextInt(255));
		}
	}
	
	@Test
	@Ignore
	public void testPerformance() throws Exception {
		StringBuilder sb = new StringBuilder();				
		final ArrayList<String> testingData = new ArrayList<String>();
		for(int i=0; i < TEST_COUNT; i++) {
			newStringData(CHARACTER_COUNT,sb);
			testingData.add(sb.toString());
		}			
		
		long startTime = System.currentTimeMillis();
		for(String it: testingData) {
			sb.setLength(0);
			sb.append(it);
			StringBuilderUtils.trim(sb);
			StringBuilderUtils.toUpperCase(sb);
		}
		final long stringBuilderTime = System.currentTimeMillis() - startTime;		
		
		sb = new StringBuilder();				
		startTime = System.currentTimeMillis();
		for(String it: testingData) {
			it.trim().toUpperCase();
		}
		final long stringTime = System.currentTimeMillis() - startTime;
		
		assertTrue("StringBuilderUtils slower then java.lang.String", stringTime >= stringBuilderTime);
	}
}

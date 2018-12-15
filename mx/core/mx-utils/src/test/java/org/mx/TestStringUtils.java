package org.mx;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestStringUtils {

	@Test
	public void testIsBlank() {
		String[] blanks = {null , "", "   ", "null", "Null", "NULL", "undefined", "UNDEFINED", "Undefined"};
		String[] notBlanks = {"a", "1", " a", " a ", " 1 ", "a13 ", " a13   "};
		for (String str : blanks) {
			assertTrue(StringUtils.isBlank(str));
		}
		for (String str : notBlanks) {
			assertFalse(StringUtils.isBlank(str));
		}
	}

	@Test
	public void testTruncate() {
		String[] src = { null, "", "123", "abc", "ABC", "中华人", "12345", "abcde", "ABCDE", "中华人民共", "1234567890",
				"abcdefghij", "ABCDEFGHIJK", "中华人民共和国万岁！" };
		String[] tar = { null, "", "123", "abc", "ABC", "中华人", "12345", "abcde", "ABCDE", "中华人民共", "12345...",
				"abcde...", "ABCDE...", "中华人民共..." };
		for (int index =0; index < src.length; index ++) {
			assertEquals(tar[index], StringUtils.truncate(src[index], 5));
		}
	}

	@Test
	public void testRepeat() {
		assertEquals("", StringUtils.repeat(0, null));
		assertEquals(" ", StringUtils.repeat(1, null));
		assertEquals(" ", StringUtils.repeat(1, " "));
		assertEquals(",", StringUtils.repeat(1, ",1a"));
		assertEquals("  ", StringUtils.repeat(2, null));
		assertEquals("  ", StringUtils.repeat(2, " "));
		assertEquals(",1", StringUtils.repeat(2, ",1a"));
		assertEquals("   ", StringUtils.repeat(3, null));
		assertEquals("   ", StringUtils.repeat(3, " "));
		assertEquals(",1a", StringUtils.repeat(3, ",1a"));
		assertEquals("       ", StringUtils.repeat(7, null));
		assertEquals("       ", StringUtils.repeat(7, " "));
		assertEquals(",1a,1a,", StringUtils.repeat(7, ",1a"));
	}
}

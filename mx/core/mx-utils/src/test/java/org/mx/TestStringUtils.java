package org.mx;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestStringUtils {

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

}

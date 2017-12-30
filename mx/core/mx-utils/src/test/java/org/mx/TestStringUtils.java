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

	@Test
    public void testString2Boolean() {
        assertEquals(false, StringUtils.string2Boolean(null, false));
        assertEquals(false, StringUtils.string2Boolean("", false));
        assertEquals(false, StringUtils.string2Boolean("abc", false));
        assertEquals(false, StringUtils.string2Boolean("truea", false));
        assertEquals(false, StringUtils.string2Boolean("falsea", false));
        assertEquals(false, StringUtils.string2Boolean("Truead", false));
        assertEquals(true, StringUtils.string2Boolean("Truead", true));
        assertEquals(true, StringUtils.string2Boolean("true", false));
        assertEquals(true, StringUtils.string2Boolean("TRUE", false));
        assertEquals(true, StringUtils.string2Boolean("True", false));
        assertEquals(true, StringUtils.string2Boolean("TRUe", false));
        assertEquals(true, StringUtils.string2Boolean("trUE", false));
        assertEquals(false, StringUtils.string2Boolean("false", false));
        assertEquals(false, StringUtils.string2Boolean("false", true));
        assertEquals(false, StringUtils.string2Boolean("FALSE", true));
        assertEquals(false, StringUtils.string2Boolean("FALSe", true));
        assertEquals(false, StringUtils.string2Boolean("falsE", true));
        assertEquals(false, StringUtils.string2Boolean("faLSe", true));
    }

    @Test
    public void testString2Int() {
        assertEquals(-1, StringUtils.string2Int(null, StringUtils.Radix.Decimal, -1));
        assertEquals(-1, StringUtils.string2Int("", StringUtils.Radix.Decimal, -1));
        assertEquals(-1, StringUtils.string2Int("10asd", StringUtils.Radix.Decimal, -1));
        assertEquals(-1, StringUtils.string2Int("asd230das", StringUtils.Radix.Decimal, -1));
        assertEquals(0, StringUtils.string2Int("0", StringUtils.Radix.Decimal, -1));
        assertEquals(12, StringUtils.string2Int("12", StringUtils.Radix.Decimal, -1));
        assertEquals(23434, StringUtils.string2Int("23434", StringUtils.Radix.Decimal, -1));
        assertEquals(123, StringUtils.string2Int("0123", StringUtils.Radix.Decimal, -1));
        assertEquals(12340, StringUtils.string2Int("012340", StringUtils.Radix.Decimal, -1));

        assertEquals(-1, StringUtils.string2Int(null, StringUtils.Radix.Hexadecimal, -1));
        assertEquals(-1, StringUtils.string2Int("", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(-1, StringUtils.string2Int("0x", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(0, StringUtils.string2Int("0x0", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(1, StringUtils.string2Int("0x1", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(1, StringUtils.string2Int("0X1", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(15, StringUtils.string2Int("0xf", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(15, StringUtils.string2Int("0xF", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(16, StringUtils.string2Int("0x10", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(255, StringUtils.string2Int("0xff", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(0, StringUtils.string2Int("0", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(1, StringUtils.string2Int("1", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(1, StringUtils.string2Int("1", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(15, StringUtils.string2Int("f", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(15, StringUtils.string2Int("F", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(16, StringUtils.string2Int("10", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(255, StringUtils.string2Int("ff", StringUtils.Radix.Hexadecimal, -1));

        assertEquals(-1, StringUtils.string2Int(null, StringUtils.Radix.Binary, -1));
        assertEquals(-1, StringUtils.string2Int("", StringUtils.Radix.Binary, -1));
        assertEquals(-1, StringUtils.string2Int("0b", StringUtils.Radix.Binary, -1));
        assertEquals(0, StringUtils.string2Int("0b0", StringUtils.Radix.Binary, -1));
        assertEquals(1, StringUtils.string2Int("0b1", StringUtils.Radix.Binary, -1));
        assertEquals(1, StringUtils.string2Int("0B1", StringUtils.Radix.Binary, -1));
        assertEquals(2, StringUtils.string2Int("0b10", StringUtils.Radix.Binary, -1));
        assertEquals(3, StringUtils.string2Int("0b11", StringUtils.Radix.Binary, -1));
        assertEquals(15, StringUtils.string2Int("0b1111", StringUtils.Radix.Binary, -1));
        assertEquals(16, StringUtils.string2Int("0b10000", StringUtils.Radix.Binary, -1));
        assertEquals(0, StringUtils.string2Int("0", StringUtils.Radix.Binary, -1));
        assertEquals(1, StringUtils.string2Int("1", StringUtils.Radix.Binary, -1));
        assertEquals(1, StringUtils.string2Int("1", StringUtils.Radix.Binary, -1));
        assertEquals(2, StringUtils.string2Int("10", StringUtils.Radix.Binary, -1));
        assertEquals(3, StringUtils.string2Int("11", StringUtils.Radix.Binary, -1));
        assertEquals(15, StringUtils.string2Int("1111", StringUtils.Radix.Binary, -1));
        assertEquals(16, StringUtils.string2Int("10000", StringUtils.Radix.Binary, -1));

        assertEquals(-1, StringUtils.string2Int(null, StringUtils.Radix.Octonary, -1));
        assertEquals(-1, StringUtils.string2Int("", StringUtils.Radix.Octonary, -1));
        assertEquals(-1, StringUtils.string2Int("08", StringUtils.Radix.Octonary, -1));
        assertEquals(0, StringUtils.string2Int("080", StringUtils.Radix.Octonary, -1));
        assertEquals(1, StringUtils.string2Int("081", StringUtils.Radix.Octonary, -1));
        assertEquals(8, StringUtils.string2Int("0810", StringUtils.Radix.Octonary, -1));
        assertEquals(9, StringUtils.string2Int("0811", StringUtils.Radix.Octonary, -1));
        assertEquals(15, StringUtils.string2Int("0817", StringUtils.Radix.Octonary, -1));
        assertEquals(16, StringUtils.string2Int("0820", StringUtils.Radix.Octonary, -1));
        assertEquals(0, StringUtils.string2Int("0", StringUtils.Radix.Octonary, -1));
        assertEquals(1, StringUtils.string2Int("1", StringUtils.Radix.Octonary, -1));
        assertEquals(6, StringUtils.string2Int("6", StringUtils.Radix.Octonary, -1));
        assertEquals(8, StringUtils.string2Int("10", StringUtils.Radix.Octonary, -1));
        assertEquals(9, StringUtils.string2Int("11", StringUtils.Radix.Octonary, -1));
        assertEquals(15, StringUtils.string2Int("17", StringUtils.Radix.Octonary, -1));
        assertEquals(16, StringUtils.string2Int("20", StringUtils.Radix.Octonary, -1));
    }

    @Test
    public void testString2Long() {
        assertEquals(-1, StringUtils.string2Long(null, StringUtils.Radix.Decimal, -1));
        assertEquals(-1, StringUtils.string2Long("", StringUtils.Radix.Decimal, -1));
        assertEquals(-1, StringUtils.string2Long("10asd", StringUtils.Radix.Decimal, -1));
        assertEquals(-1, StringUtils.string2Long("asd230das", StringUtils.Radix.Decimal, -1));
        assertEquals(0, StringUtils.string2Long("0", StringUtils.Radix.Decimal, -1));
        assertEquals(12, StringUtils.string2Long("12", StringUtils.Radix.Decimal, -1));
        assertEquals(23434, StringUtils.string2Long("23434", StringUtils.Radix.Decimal, -1));
        assertEquals(123, StringUtils.string2Long("0123", StringUtils.Radix.Decimal, -1));
        assertEquals(12340, StringUtils.string2Long("012340", StringUtils.Radix.Decimal, -1));

        assertEquals(-1, StringUtils.string2Long(null, StringUtils.Radix.Hexadecimal, -1));
        assertEquals(-1, StringUtils.string2Long("", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(-1, StringUtils.string2Long("0x", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(0, StringUtils.string2Long("0x0", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(1, StringUtils.string2Long("0x1", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(1, StringUtils.string2Long("0X1", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(15, StringUtils.string2Long("0xf", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(15, StringUtils.string2Long("0xF", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(16, StringUtils.string2Long("0x10", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(255, StringUtils.string2Long("0xff", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(0, StringUtils.string2Long("0", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(1, StringUtils.string2Long("1", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(1, StringUtils.string2Long("1", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(15, StringUtils.string2Long("f", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(15, StringUtils.string2Long("F", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(16, StringUtils.string2Long("10", StringUtils.Radix.Hexadecimal, -1));
        assertEquals(255, StringUtils.string2Long("ff", StringUtils.Radix.Hexadecimal, -1));

        assertEquals(-1, StringUtils.string2Long(null, StringUtils.Radix.Binary, -1));
        assertEquals(-1, StringUtils.string2Long("", StringUtils.Radix.Binary, -1));
        assertEquals(-1, StringUtils.string2Long("0b", StringUtils.Radix.Binary, -1));
        assertEquals(0, StringUtils.string2Long("0b0", StringUtils.Radix.Binary, -1));
        assertEquals(1, StringUtils.string2Long("0b1", StringUtils.Radix.Binary, -1));
        assertEquals(1, StringUtils.string2Long("0B1", StringUtils.Radix.Binary, -1));
        assertEquals(2, StringUtils.string2Long("0b10", StringUtils.Radix.Binary, -1));
        assertEquals(3, StringUtils.string2Long("0b11", StringUtils.Radix.Binary, -1));
        assertEquals(15, StringUtils.string2Long("0b1111", StringUtils.Radix.Binary, -1));
        assertEquals(16, StringUtils.string2Long("0b10000", StringUtils.Radix.Binary, -1));
        assertEquals(0, StringUtils.string2Long("0", StringUtils.Radix.Binary, -1));
        assertEquals(1, StringUtils.string2Long("1", StringUtils.Radix.Binary, -1));
        assertEquals(1, StringUtils.string2Long("1", StringUtils.Radix.Binary, -1));
        assertEquals(2, StringUtils.string2Long("10", StringUtils.Radix.Binary, -1));
        assertEquals(3, StringUtils.string2Long("11", StringUtils.Radix.Binary, -1));
        assertEquals(15, StringUtils.string2Long("1111", StringUtils.Radix.Binary, -1));
        assertEquals(16, StringUtils.string2Long("10000", StringUtils.Radix.Binary, -1));

        assertEquals(-1, StringUtils.string2Long(null, StringUtils.Radix.Octonary, -1));
        assertEquals(-1, StringUtils.string2Long("", StringUtils.Radix.Octonary, -1));
        assertEquals(-1, StringUtils.string2Long("08", StringUtils.Radix.Octonary, -1));
        assertEquals(0, StringUtils.string2Long("080", StringUtils.Radix.Octonary, -1));
        assertEquals(1, StringUtils.string2Long("081", StringUtils.Radix.Octonary, -1));
        assertEquals(8, StringUtils.string2Long("0810", StringUtils.Radix.Octonary, -1));
        assertEquals(9, StringUtils.string2Long("0811", StringUtils.Radix.Octonary, -1));
        assertEquals(15, StringUtils.string2Long("0817", StringUtils.Radix.Octonary, -1));
        assertEquals(16, StringUtils.string2Long("0820", StringUtils.Radix.Octonary, -1));
        assertEquals(0, StringUtils.string2Long("0", StringUtils.Radix.Octonary, -1));
        assertEquals(1, StringUtils.string2Long("1", StringUtils.Radix.Octonary, -1));
        assertEquals(6, StringUtils.string2Long("6", StringUtils.Radix.Octonary, -1));
        assertEquals(8, StringUtils.string2Long("10", StringUtils.Radix.Octonary, -1));
        assertEquals(9, StringUtils.string2Long("11", StringUtils.Radix.Octonary, -1));
        assertEquals(15, StringUtils.string2Long("17", StringUtils.Radix.Octonary, -1));
        assertEquals(16, StringUtils.string2Long("20", StringUtils.Radix.Octonary, -1));
    }

    @Test
    public void testString2Float() {
        assertEquals(0, StringUtils.string2Float(null, 0), 5);
        assertEquals(0, StringUtils.string2Float("", 0), 5);
        assertEquals(0, StringUtils.string2Float("0", 0), 5);
        assertEquals(0.1, StringUtils.string2Float("0.1", 0), 5);
        assertEquals(1.1234, StringUtils.string2Float("1.1234", 0), 5);
        assertEquals(1.12345, StringUtils.string2Float("1.12345", 0), 5);
        assertEquals(1.12346, StringUtils.string2Float("1.123456", 0), 5);
        assertEquals(1.12346, StringUtils.string2Float("1.123456789", 0), 5);
        assertEquals(1234.12345, StringUtils.string2Float("1234.12345", 0), 5);
        assertEquals(1234.12346, StringUtils.string2Float("1234.1234567", 0), 5);
        assertEquals(1234.123451, StringUtils.string2Float("1234.12345123", 0), 6);
    }

    @Test
    public void testString2Double() {
        assertEquals(0, StringUtils.string2Double(null, 0), 5);
        assertEquals(0, StringUtils.string2Double("", 0), 5);
        assertEquals(0, StringUtils.string2Double("0", 0), 5);
        assertEquals(0.1, StringUtils.string2Double("0.1", 0), 5);
        assertEquals(1.1234, StringUtils.string2Double("1.1234", 0), 5);
        assertEquals(1.12345, StringUtils.string2Double("1.12345", 0), 5);
        assertEquals(1.12346, StringUtils.string2Double("1.123456", 0), 5);
        assertEquals(1.12346, StringUtils.string2Double("1.123456789", 0), 5);
        assertEquals(1234.12345, StringUtils.string2Double("1234.12345", 0), 5);
        assertEquals(1234.12346, StringUtils.string2Double("1234.1234567", 0), 5);
        assertEquals(1234.123451, StringUtils.string2Double("1234.12345123", 0), 6);
    }

    @Test
    public void testString2Size() {
        assertEquals(-1, StringUtils.string2Size(null, -1));
        assertEquals(-1, StringUtils.string2Size("", -1));
        assertEquals(0, StringUtils.string2Size("0", -1));
        assertEquals(0, StringUtils.string2Size("0K", -1));
        assertEquals(0, StringUtils.string2Size("0M", -1));
        assertEquals(0, StringUtils.string2Size("0G", -1));
        assertEquals(0, StringUtils.string2Size("0T", -1));
        assertEquals(0, StringUtils.string2Size("0P", -1));
        assertEquals(12345, StringUtils.string2Size("12345", -1));
        assertEquals(1 * StringUtils.KB, StringUtils.string2Size("1K", -1));
        assertEquals(1 * StringUtils.MB, StringUtils.string2Size("1M", -1));
        assertEquals(1 * StringUtils.GB, StringUtils.string2Size("1G", -1));
        assertEquals(1 * StringUtils.TB, StringUtils.string2Size("1T", -1));
        assertEquals(1 * StringUtils.PB, StringUtils.string2Size("1P", -1));
        assertEquals(1 * StringUtils.KB, StringUtils.string2Size("1 K", -1));
        assertEquals(1 * StringUtils.MB, StringUtils.string2Size("1 M", -1));
        assertEquals(1 * StringUtils.GB, StringUtils.string2Size("1 G", -1));
        assertEquals(1 * StringUtils.TB, StringUtils.string2Size("1 T", -1));
        assertEquals(1 * StringUtils.PB, StringUtils.string2Size("1 P", -1));
        assertEquals(1 * StringUtils.KB, StringUtils.string2Size("1k", -1));
        assertEquals(1 * StringUtils.MB, StringUtils.string2Size("1m", -1));
        assertEquals(1 * StringUtils.GB, StringUtils.string2Size("1g", -1));
        assertEquals(1 * StringUtils.TB, StringUtils.string2Size("1t", -1));
        assertEquals(1 * StringUtils.PB, StringUtils.string2Size("1p", -1));
        assertEquals(0.5 * StringUtils.KB, StringUtils.string2Size("0.5k", -1), 3);
        assertEquals(1.7 * StringUtils.KB, StringUtils.string2Size("1.7k", -1), 3);
        assertEquals(6.843 * StringUtils.KB, StringUtils.string2Size("6.843k", -1), 3);
    }
}

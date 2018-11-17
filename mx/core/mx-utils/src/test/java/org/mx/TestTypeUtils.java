package org.mx;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class TestTypeUtils {
    @Test
    public void testEquals() {
        String str1 = "12";
        int i1 = 12;
        Integer I1 = 12;
        long l1 = 12;
        Long L1 = 12L;
        float f1 = 12.12F;
        Float F1 = 12.12F;
        double d1 = 12.12;
        Double D1 = 12.12;
        assertTrue(TypeUtils.equals(null, null));
        assertFalse(TypeUtils.equals(null, str1));
        assertFalse(TypeUtils.equals(null, i1));
        assertFalse(TypeUtils.equals(null, I1));
        assertFalse(TypeUtils.equals(null, l1));
        assertFalse(TypeUtils.equals(null, L1));
        assertFalse(TypeUtils.equals(null, f1));
        assertFalse(TypeUtils.equals(null, F1));
        assertFalse(TypeUtils.equals(null, d1));
        assertFalse(TypeUtils.equals(null, D1));
        assertTrue(TypeUtils.equals(str1, i1));
        assertTrue(TypeUtils.equals(str1, I1));
        assertTrue(TypeUtils.equals(str1, L1));
        assertTrue(TypeUtils.equals(i1, I1));
        assertTrue(TypeUtils.equals(i1, l1));
        assertTrue(TypeUtils.equals(i1, L1));
        assertTrue(TypeUtils.equals(l1, I1));
        assertTrue(TypeUtils.equals(f1, F1));
        assertTrue(TypeUtils.equals(d1, D1));
        assertTrue(TypeUtils.equals(f1, D1));
    }

    @Test
    public void testInt() {
        Random random = new Random(System.currentTimeMillis());
        for (int index = 0; index < 10; index++) {
            int i = random.nextInt();
            byte[] bytes = TypeUtils.int2ByteArray(i);
            assertEquals(4, bytes.length);
            int check = TypeUtils.byteArray2Int(bytes);
            assertEquals(i, check);
        }
    }

    @Test
    public void testLong() {
        Random random = new Random(System.currentTimeMillis());
        for (int index = 0; index < 10; index++) {
            long l = random.nextLong();
            byte[] bytes = TypeUtils.long2ByteArray(l);
            assertEquals(8, bytes.length);
            long check = TypeUtils.byteArray2Long(bytes);
            assertEquals(l, check);
        }
    }

    @Test
    public void testIpv4() {
        assertEquals("NA", TypeUtils.byteArray2Ip(null));
        assertEquals("NA", TypeUtils.byteArray2Ip(new byte[]{}));
        assertEquals("NA", TypeUtils.byteArray2Ipv4(null));
        assertEquals("NA", TypeUtils.byteArray2Ipv4(new byte[]{}));
        assertEquals("NA", TypeUtils.byteArray2Ipv6(null));
        assertEquals("NA", TypeUtils.byteArray2Ipv6(new byte[]{}));
        assertEquals("127.0.0.1", TypeUtils.byteArray2Ip(new byte[]{(byte) 0x7f, 0, 0, 1}));
        assertEquals("127.0", TypeUtils.byteArray2Ip(new byte[]{(byte) 0x7f, 0}));
        assertEquals("192.168.0.248", TypeUtils.byteArray2Ip(new byte[]{(byte) 0xc0, (byte) 0xa8, 0, (byte) 0xf8}));
        assertEquals("NA", TypeUtils.byteArray2Ipv6(new byte[]{(byte) 0x7f, 0, 0, 1}));
        assertEquals("NA", TypeUtils.byteArray2Ipv6(new byte[]{(byte) 0x7f, 0}));
        assertEquals("NA", TypeUtils.byteArray2Ipv6(new byte[]{(byte) 0xc0, (byte) 0xa8, 0, (byte) 0xf8}));
        assertEquals("::1", TypeUtils.byteArray2Ip(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}));
        assertEquals("fe80::18c6:7df6:18f0:502f", TypeUtils.byteArray2Ip(new byte[]{(byte) 0xfe, (byte) 0x80, 0, 0, 0, 0, 0, 0,
                (byte) 0x18, (byte) 0xc6, (byte) 0x7d, (byte) 0xf6, (byte) 0x18, (byte) 0xf0, (byte) 0x50, (byte) 0x2f}));

        assertArrayEquals(new byte[]{}, TypeUtils.Ip2byteArray(null));
        assertArrayEquals(new byte[]{}, TypeUtils.Ip2byteArray(""));
        assertArrayEquals(new byte[]{}, TypeUtils.Ip2byteArrayV4(null));
        assertArrayEquals(new byte[]{}, TypeUtils.Ip2byteArrayV4(""));
        assertArrayEquals(new byte[]{}, TypeUtils.Ip2byteArrayV6(null));
        assertArrayEquals(new byte[]{}, TypeUtils.Ip2byteArrayV6(""));
        assertArrayEquals(new byte[]{(byte) 0x7f, 0, 0, 1}, TypeUtils.Ip2byteArray("127.0.0.1"));
        assertArrayEquals(new byte[]{(byte) 0x7f, 0}, TypeUtils.Ip2byteArray("127.0"));
        assertArrayEquals(new byte[]{(byte) 0xc0, (byte) 0xa8, 0, (byte) 0xf8}, TypeUtils.Ip2byteArray("192.168.0.248"));
        assertArrayEquals(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, TypeUtils.Ip2byteArray("::1"));
        assertArrayEquals(new byte[]{(byte) 0xfe, (byte) 0x80, 0, 0, 0, 0, 0, 0,
                        (byte) 0x18, (byte) 0xc6, (byte) 0x7d, (byte) 0xf6, (byte) 0x18, (byte) 0xf0, (byte) 0x50, (byte) 0x2f},
                TypeUtils.Ip2byteArray("fe80::18c6:7df6:18f0:502f"));
    }


    @Test
    public void testString2Boolean() {
        assertFalse(TypeUtils.string2Boolean(null, false));
        assertFalse(TypeUtils.string2Boolean("", false));
        assertFalse(TypeUtils.string2Boolean("abc", false));
        assertFalse(TypeUtils.string2Boolean("truea", false));
        assertFalse(TypeUtils.string2Boolean("falsea", false));
        assertFalse(TypeUtils.string2Boolean("Truead", false));
        assertTrue(TypeUtils.string2Boolean("Truead", true));
        assertTrue(TypeUtils.string2Boolean("true", false));
        assertTrue(TypeUtils.string2Boolean("TRUE", false));
        assertTrue(TypeUtils.string2Boolean("True", false));
        assertTrue(TypeUtils.string2Boolean("TRUe", false));
        assertTrue(TypeUtils.string2Boolean("trUE", false));
        assertFalse(TypeUtils.string2Boolean("false", false));
        assertFalse(TypeUtils.string2Boolean("false", true));
        assertFalse(TypeUtils.string2Boolean("FALSE", true));
        assertFalse(TypeUtils.string2Boolean("FALSe", true));
        assertFalse(TypeUtils.string2Boolean("falsE", true));
        assertFalse(TypeUtils.string2Boolean("faLSe", true));
    }

    @Test
    public void testString2Int() {
        assertEquals(-1, TypeUtils.string2Int(null, TypeUtils.Radix.Decimal, -1));
        assertEquals(-1, TypeUtils.string2Int("", TypeUtils.Radix.Decimal, -1));
        assertEquals(-1, TypeUtils.string2Int("10asd", TypeUtils.Radix.Decimal, -1));
        assertEquals(-1, TypeUtils.string2Int("asd230das", TypeUtils.Radix.Decimal, -1));
        assertEquals(0, TypeUtils.string2Int("0", TypeUtils.Radix.Decimal, -1));
        assertEquals(12, TypeUtils.string2Int("12", TypeUtils.Radix.Decimal, -1));
        assertEquals(23434, TypeUtils.string2Int("23434", TypeUtils.Radix.Decimal, -1));
        assertEquals(123, TypeUtils.string2Int("0123", TypeUtils.Radix.Decimal, -1));
        assertEquals(12340, TypeUtils.string2Int("012340", TypeUtils.Radix.Decimal, -1));

        assertEquals(-1, TypeUtils.string2Int(null, TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(-1, TypeUtils.string2Int("", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(-1, TypeUtils.string2Int("0x", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(0, TypeUtils.string2Int("0x0", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(1, TypeUtils.string2Int("0x1", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(1, TypeUtils.string2Int("0X1", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(15, TypeUtils.string2Int("0xf", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(15, TypeUtils.string2Int("0xF", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(16, TypeUtils.string2Int("0x10", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(255, TypeUtils.string2Int("0xff", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(0, TypeUtils.string2Int("0", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(1, TypeUtils.string2Int("1", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(1, TypeUtils.string2Int("1", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(15, TypeUtils.string2Int("f", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(15, TypeUtils.string2Int("F", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(16, TypeUtils.string2Int("10", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(255, TypeUtils.string2Int("ff", TypeUtils.Radix.Hexadecimal, -1));

        assertEquals(-1, TypeUtils.string2Int(null, TypeUtils.Radix.Binary, -1));
        assertEquals(-1, TypeUtils.string2Int("", TypeUtils.Radix.Binary, -1));
        assertEquals(-1, TypeUtils.string2Int("0b", TypeUtils.Radix.Binary, -1));
        assertEquals(0, TypeUtils.string2Int("0b0", TypeUtils.Radix.Binary, -1));
        assertEquals(1, TypeUtils.string2Int("0b1", TypeUtils.Radix.Binary, -1));
        assertEquals(1, TypeUtils.string2Int("0B1", TypeUtils.Radix.Binary, -1));
        assertEquals(2, TypeUtils.string2Int("0b10", TypeUtils.Radix.Binary, -1));
        assertEquals(3, TypeUtils.string2Int("0b11", TypeUtils.Radix.Binary, -1));
        assertEquals(15, TypeUtils.string2Int("0b1111", TypeUtils.Radix.Binary, -1));
        assertEquals(16, TypeUtils.string2Int("0b10000", TypeUtils.Radix.Binary, -1));
        assertEquals(0, TypeUtils.string2Int("0", TypeUtils.Radix.Binary, -1));
        assertEquals(1, TypeUtils.string2Int("1", TypeUtils.Radix.Binary, -1));
        assertEquals(1, TypeUtils.string2Int("1", TypeUtils.Radix.Binary, -1));
        assertEquals(2, TypeUtils.string2Int("10", TypeUtils.Radix.Binary, -1));
        assertEquals(3, TypeUtils.string2Int("11", TypeUtils.Radix.Binary, -1));
        assertEquals(15, TypeUtils.string2Int("1111", TypeUtils.Radix.Binary, -1));
        assertEquals(16, TypeUtils.string2Int("10000", TypeUtils.Radix.Binary, -1));

        assertEquals(-1, TypeUtils.string2Int(null, TypeUtils.Radix.Octonary, -1));
        assertEquals(-1, TypeUtils.string2Int("", TypeUtils.Radix.Octonary, -1));
        assertEquals(-1, TypeUtils.string2Int("08", TypeUtils.Radix.Octonary, -1));
        assertEquals(0, TypeUtils.string2Int("080", TypeUtils.Radix.Octonary, -1));
        assertEquals(1, TypeUtils.string2Int("081", TypeUtils.Radix.Octonary, -1));
        assertEquals(8, TypeUtils.string2Int("0810", TypeUtils.Radix.Octonary, -1));
        assertEquals(9, TypeUtils.string2Int("0811", TypeUtils.Radix.Octonary, -1));
        assertEquals(15, TypeUtils.string2Int("0817", TypeUtils.Radix.Octonary, -1));
        assertEquals(16, TypeUtils.string2Int("0820", TypeUtils.Radix.Octonary, -1));
        assertEquals(0, TypeUtils.string2Int("0", TypeUtils.Radix.Octonary, -1));
        assertEquals(1, TypeUtils.string2Int("1", TypeUtils.Radix.Octonary, -1));
        assertEquals(6, TypeUtils.string2Int("6", TypeUtils.Radix.Octonary, -1));
        assertEquals(8, TypeUtils.string2Int("10", TypeUtils.Radix.Octonary, -1));
        assertEquals(9, TypeUtils.string2Int("11", TypeUtils.Radix.Octonary, -1));
        assertEquals(15, TypeUtils.string2Int("17", TypeUtils.Radix.Octonary, -1));
        assertEquals(16, TypeUtils.string2Int("20", TypeUtils.Radix.Octonary, -1));
    }

    @Test
    public void testString2Long() {
        assertEquals(-1, TypeUtils.string2Long(null, TypeUtils.Radix.Decimal, -1));
        assertEquals(-1, TypeUtils.string2Long("", TypeUtils.Radix.Decimal, -1));
        assertEquals(-1, TypeUtils.string2Long("10asd", TypeUtils.Radix.Decimal, -1));
        assertEquals(-1, TypeUtils.string2Long("asd230das", TypeUtils.Radix.Decimal, -1));
        assertEquals(0, TypeUtils.string2Long("0", TypeUtils.Radix.Decimal, -1));
        assertEquals(12, TypeUtils.string2Long("12", TypeUtils.Radix.Decimal, -1));
        assertEquals(23434, TypeUtils.string2Long("23434", TypeUtils.Radix.Decimal, -1));
        assertEquals(123, TypeUtils.string2Long("0123", TypeUtils.Radix.Decimal, -1));
        assertEquals(12340, TypeUtils.string2Long("012340", TypeUtils.Radix.Decimal, -1));

        assertEquals(-1, TypeUtils.string2Long(null, TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(-1, TypeUtils.string2Long("", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(-1, TypeUtils.string2Long("0x", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(0, TypeUtils.string2Long("0x0", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(1, TypeUtils.string2Long("0x1", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(1, TypeUtils.string2Long("0X1", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(15, TypeUtils.string2Long("0xf", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(15, TypeUtils.string2Long("0xF", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(16, TypeUtils.string2Long("0x10", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(255, TypeUtils.string2Long("0xff", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(0, TypeUtils.string2Long("0", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(1, TypeUtils.string2Long("1", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(1, TypeUtils.string2Long("1", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(15, TypeUtils.string2Long("f", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(15, TypeUtils.string2Long("F", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(16, TypeUtils.string2Long("10", TypeUtils.Radix.Hexadecimal, -1));
        assertEquals(255, TypeUtils.string2Long("ff", TypeUtils.Radix.Hexadecimal, -1));

        assertEquals(-1, TypeUtils.string2Long(null, TypeUtils.Radix.Binary, -1));
        assertEquals(-1, TypeUtils.string2Long("", TypeUtils.Radix.Binary, -1));
        assertEquals(-1, TypeUtils.string2Long("0b", TypeUtils.Radix.Binary, -1));
        assertEquals(0, TypeUtils.string2Long("0b0", TypeUtils.Radix.Binary, -1));
        assertEquals(1, TypeUtils.string2Long("0b1", TypeUtils.Radix.Binary, -1));
        assertEquals(1, TypeUtils.string2Long("0B1", TypeUtils.Radix.Binary, -1));
        assertEquals(2, TypeUtils.string2Long("0b10", TypeUtils.Radix.Binary, -1));
        assertEquals(3, TypeUtils.string2Long("0b11", TypeUtils.Radix.Binary, -1));
        assertEquals(15, TypeUtils.string2Long("0b1111", TypeUtils.Radix.Binary, -1));
        assertEquals(16, TypeUtils.string2Long("0b10000", TypeUtils.Radix.Binary, -1));
        assertEquals(0, TypeUtils.string2Long("0", TypeUtils.Radix.Binary, -1));
        assertEquals(1, TypeUtils.string2Long("1", TypeUtils.Radix.Binary, -1));
        assertEquals(1, TypeUtils.string2Long("1", TypeUtils.Radix.Binary, -1));
        assertEquals(2, TypeUtils.string2Long("10", TypeUtils.Radix.Binary, -1));
        assertEquals(3, TypeUtils.string2Long("11", TypeUtils.Radix.Binary, -1));
        assertEquals(15, TypeUtils.string2Long("1111", TypeUtils.Radix.Binary, -1));
        assertEquals(16, TypeUtils.string2Long("10000", TypeUtils.Radix.Binary, -1));

        assertEquals(-1, TypeUtils.string2Long(null, TypeUtils.Radix.Octonary, -1));
        assertEquals(-1, TypeUtils.string2Long("", TypeUtils.Radix.Octonary, -1));
        assertEquals(-1, TypeUtils.string2Long("08", TypeUtils.Radix.Octonary, -1));
        assertEquals(0, TypeUtils.string2Long("080", TypeUtils.Radix.Octonary, -1));
        assertEquals(1, TypeUtils.string2Long("081", TypeUtils.Radix.Octonary, -1));
        assertEquals(8, TypeUtils.string2Long("0810", TypeUtils.Radix.Octonary, -1));
        assertEquals(9, TypeUtils.string2Long("0811", TypeUtils.Radix.Octonary, -1));
        assertEquals(15, TypeUtils.string2Long("0817", TypeUtils.Radix.Octonary, -1));
        assertEquals(16, TypeUtils.string2Long("0820", TypeUtils.Radix.Octonary, -1));
        assertEquals(0, TypeUtils.string2Long("0", TypeUtils.Radix.Octonary, -1));
        assertEquals(1, TypeUtils.string2Long("1", TypeUtils.Radix.Octonary, -1));
        assertEquals(6, TypeUtils.string2Long("6", TypeUtils.Radix.Octonary, -1));
        assertEquals(8, TypeUtils.string2Long("10", TypeUtils.Radix.Octonary, -1));
        assertEquals(9, TypeUtils.string2Long("11", TypeUtils.Radix.Octonary, -1));
        assertEquals(15, TypeUtils.string2Long("17", TypeUtils.Radix.Octonary, -1));
        assertEquals(16, TypeUtils.string2Long("20", TypeUtils.Radix.Octonary, -1));
    }

    @Test
    public void testString2Float() {
        assertEquals(0, TypeUtils.string2Float(null, 0), 5);
        assertEquals(0, TypeUtils.string2Float("", 0), 5);
        assertEquals(0, TypeUtils.string2Float("0", 0), 5);
        assertEquals(0.1, TypeUtils.string2Float("0.1", 0), 5);
        assertEquals(1.1234, TypeUtils.string2Float("1.1234", 0), 5);
        assertEquals(1.12345, TypeUtils.string2Float("1.12345", 0), 5);
        assertEquals(1.12346, TypeUtils.string2Float("1.123456", 0), 5);
        assertEquals(1.12346, TypeUtils.string2Float("1.123456789", 0), 5);
        assertEquals(1234.12345, TypeUtils.string2Float("1234.12345", 0), 5);
        assertEquals(1234.12346, TypeUtils.string2Float("1234.1234567", 0), 5);
        assertEquals(1234.123451, TypeUtils.string2Float("1234.12345123", 0), 6);
    }

    @Test
    public void testString2Double() {
        assertEquals(0, TypeUtils.string2Double(null, 0), 5);
        assertEquals(0, TypeUtils.string2Double("", 0), 5);
        assertEquals(0, TypeUtils.string2Double("0", 0), 5);
        assertEquals(0.1, TypeUtils.string2Double("0.1", 0), 5);
        assertEquals(1.1234, TypeUtils.string2Double("1.1234", 0), 5);
        assertEquals(1.12345, TypeUtils.string2Double("1.12345", 0), 5);
        assertEquals(1.12346, TypeUtils.string2Double("1.123456", 0), 5);
        assertEquals(1.12346, TypeUtils.string2Double("1.123456789", 0), 5);
        assertEquals(1234.12345, TypeUtils.string2Double("1234.12345", 0), 5);
        assertEquals(1234.12346, TypeUtils.string2Double("1234.1234567", 0), 5);
        assertEquals(1234.123451, TypeUtils.string2Double("1234.12345123", 0), 6);
    }

    @Test
    public void testString2Size() {
        assertEquals(-1, TypeUtils.string2Size(null, -1));
        assertEquals(-1, TypeUtils.string2Size("", -1));
        assertEquals(0, TypeUtils.string2Size("0", -1));
        assertEquals(0, TypeUtils.string2Size("0K", -1));
        assertEquals(0, TypeUtils.string2Size("0Mb", -1));
        assertEquals(0, TypeUtils.string2Size("0G", -1));
        assertEquals(0, TypeUtils.string2Size("0T", -1));
        assertEquals(0, TypeUtils.string2Size("0P", -1));
        assertEquals(12345, TypeUtils.string2Size("12345", -1));
        assertEquals(TypeUtils.KB, TypeUtils.string2Size("1K", -1));
        assertEquals(TypeUtils.MB, TypeUtils.string2Size("1M", -1));
        assertEquals(TypeUtils.GB, TypeUtils.string2Size("1GB", -1));
        assertEquals(TypeUtils.TB, TypeUtils.string2Size("1T", -1));
        assertEquals(TypeUtils.PB, TypeUtils.string2Size("1Pb", -1));
        assertEquals(TypeUtils.KB, TypeUtils.string2Size("1 K", -1));
        assertEquals(TypeUtils.MB, TypeUtils.string2Size("1 M", -1));
        assertEquals(TypeUtils.GB, TypeUtils.string2Size("1 G", -1));
        assertEquals(TypeUtils.TB, TypeUtils.string2Size("1 T", -1));
        assertEquals(TypeUtils.PB, TypeUtils.string2Size("1 P", -1));
        assertEquals(TypeUtils.KB, TypeUtils.string2Size("1kb", -1));
        assertEquals(TypeUtils.MB, TypeUtils.string2Size("1m", -1));
        assertEquals(TypeUtils.GB, TypeUtils.string2Size("1gB", -1));
        assertEquals(TypeUtils.TB, TypeUtils.string2Size("1t", -1));
        assertEquals(TypeUtils.PB, TypeUtils.string2Size("1p", -1));
        assertEquals(0.5 * TypeUtils.KB, TypeUtils.string2Size("0.5k", -1), 3);
        assertEquals(1.7 * TypeUtils.KB, TypeUtils.string2Size("1.7k", -1), 3);
        assertEquals(6.843 * TypeUtils.KB, TypeUtils.string2Size("6.843k", -1), 3);
    }

    @Test
    public void testString2TimePeriod() {
        assertEquals(-1, TypeUtils.string2TimePeriod(null, -1));
        assertEquals(-1, TypeUtils.string2TimePeriod("", -1));
        assertEquals(0, TypeUtils.string2TimePeriod("0", -1));
        assertEquals(0, TypeUtils.string2TimePeriod("0SEC", -1));
        assertEquals(0, TypeUtils.string2TimePeriod("0MIN", -1));
        assertEquals(0, TypeUtils.string2TimePeriod("0HOUR", -1));
        assertEquals(0, TypeUtils.string2TimePeriod("0DAY", -1));
        assertEquals(0, TypeUtils.string2TimePeriod("0WEEK", -1));
        assertEquals(0, TypeUtils.string2TimePeriod("0MON", -1));
        assertEquals(0, TypeUtils.string2TimePeriod("0QUAR", -1));
        assertEquals(0, TypeUtils.string2TimePeriod("0YEAR", -1));
        assertEquals(12345, TypeUtils.string2TimePeriod("12345", -1));
        assertEquals(TypeUtils.SEC, TypeUtils.string2TimePeriod("1SEC", -1));
        assertEquals(TypeUtils.MIN, TypeUtils.string2TimePeriod("1MIN", -1));
        assertEquals(TypeUtils.HOUR, TypeUtils.string2TimePeriod("1HOUR", -1));
        assertEquals(TypeUtils.DAY, TypeUtils.string2TimePeriod("1DAY", -1));
        assertEquals(TypeUtils.WEEK, TypeUtils.string2TimePeriod("1WEEK", -1));
        assertEquals(TypeUtils.MON, TypeUtils.string2TimePeriod("1MON", -1));
        assertEquals(TypeUtils.QUAR, TypeUtils.string2TimePeriod("1QUAR", -1));
        assertEquals(TypeUtils.YEAR, TypeUtils.string2TimePeriod("1YEAR", -1));
        assertEquals(TypeUtils.SEC, TypeUtils.string2TimePeriod("1 SEC", -1));
        assertEquals(TypeUtils.MIN, TypeUtils.string2TimePeriod("1 MIN", -1));
        assertEquals(TypeUtils.HOUR, TypeUtils.string2TimePeriod("1 HOUR", -1));
        assertEquals(TypeUtils.DAY, TypeUtils.string2TimePeriod("1 DAY", -1));
        assertEquals(TypeUtils.WEEK, TypeUtils.string2TimePeriod("1 WEEK", -1));
        assertEquals(TypeUtils.MON, TypeUtils.string2TimePeriod("1 MON", -1));
        assertEquals(TypeUtils.QUAR, TypeUtils.string2TimePeriod("1 QUAR", -1));
        assertEquals(TypeUtils.YEAR, TypeUtils.string2TimePeriod("1 YEAR", -1));
        assertEquals(TypeUtils.SEC, TypeUtils.string2TimePeriod("1Sec", -1));
        assertEquals(TypeUtils.MIN, TypeUtils.string2TimePeriod("1Min", -1));
        assertEquals(TypeUtils.HOUR, TypeUtils.string2TimePeriod("1hoUR", -1));
        assertEquals(TypeUtils.DAY, TypeUtils.string2TimePeriod("1day", -1));
        assertEquals(TypeUtils.WEEK, TypeUtils.string2TimePeriod("1weEK", -1));
        assertEquals(TypeUtils.MON, TypeUtils.string2TimePeriod("1mon", -1));
        assertEquals(TypeUtils.QUAR, TypeUtils.string2TimePeriod("1QuaR", -1));
        assertEquals(TypeUtils.YEAR, TypeUtils.string2TimePeriod("1YeaR", -1));
        assertEquals(0.5 * TypeUtils.MON, TypeUtils.string2TimePeriod("0.5MON", -1), 3);
        assertEquals(1.7 * TypeUtils.YEAR, TypeUtils.string2TimePeriod("1.7YEAR", -1), 3);
        assertEquals(6.843 * TypeUtils.HOUR, TypeUtils.string2TimePeriod("6.843 hour", -1), 3);
    }
}

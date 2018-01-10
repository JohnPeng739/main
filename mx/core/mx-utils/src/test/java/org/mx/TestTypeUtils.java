package org.mx;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestTypeUtils {
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
        assertArrayEquals(new byte[]{1}, TypeUtils.Ip2byteArray("::1"));
        assertArrayEquals(new byte[]{(byte) 0xfe, (byte) 0x80, 0, 0, 0, 0, 0, 0,
                (byte) 0x18, (byte) 0xc6, (byte) 0x7d, (byte) 0xf6, (byte) 0x18, (byte) 0xf0, (byte) 0x50, (byte) 0x2f},
                TypeUtils.Ip2byteArray("fe80::18c6:7df6:18f0:502f"));
    }
}

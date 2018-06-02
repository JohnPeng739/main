package org.mx;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by john on 2017/10/17.
 */
public class TestDigestUtils {
    @Test
    public void testBase64() {
        assertEquals("5PWKgFpuH9D2vvWMhvnOsw==", DigestUtils.md5("I love you"));
    }

    private static long[] crc32Table = new long[256];

    static {
        long crcValue;
        for (int i = 0; i < 256; i++) {
            crcValue = i;
            for (int j = 0; j < 8; j++) {
                if ((crcValue & 1) == 1) {
                    crcValue = crcValue >> 1;
                    crcValue = 0x00000000edb88320L ^ crcValue;
                } else {
                    crcValue = crcValue >> 1;

                }
            }
            crc32Table[i] = crcValue;
        }
    }

    private static long getCrc32(byte[] bytes) {
        long resultCrcValue = 0x00000000ffffffffL;
        if (bytes != null && bytes.length > 0) {
            for (byte b : bytes) {
                int index = (int) ((resultCrcValue ^ b) & 0xff);
                resultCrcValue = crc32Table[index] ^ (resultCrcValue >> 8);
            }
        }
        resultCrcValue = resultCrcValue ^ 0x00000000ffffffffL;
        return resultCrcValue;
    }

    @Test
    public void testCrc32() {
        assertEquals(getCrc32(null), DigestUtils.crc32(null));
        String testStr = "{\"log\":{\"content\":\"2\",\"time\":\"2016-01-05 10:17:24\",\"type\":1001,\"version\":\"[5.0.8.12]\"},\"pcInfo\":{\"ip\":\"192.168.118.57\",\"mac\":\"94-DE-80-A8-E6-EC\",\"onlyId\":\"7CE81DDBF7D05F6AD89CD7D79FAA5905\"},\"user\":{\"name\":\"CFM\"}}";
        assertEquals(getCrc32(testStr.getBytes()), DigestUtils.crc32(testStr.getBytes()));

        assertEquals(getCrc32(TypeUtils.long2ByteArray(123456L)), DigestUtils.crc32(TypeUtils.long2ByteArray(123456L)));
    }
}

package org.mx;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by john on 2017/10/17.
 */
public class TestDigestUtils {
    @Test
    public void testBase64() throws Exception {
        assertEquals("5PWKgFpuH9D2vvWMhvnOsw==", DigestUtils.md5("I love you"));
    }
}

package org.mx.test;

import org.junit.Test;
import org.mx.hanlp.utils.HanlpUtils;

import static org.junit.Assert.assertEquals;

public class TestHanlpUtils {
    @Test
    public void testYinTou() {
        assertEquals("", HanlpUtils.yinTou(null));
        assertEquals("", HanlpUtils.yinTou(""));
        assertEquals("", HanlpUtils.yinTou(" "));
        assertEquals("", HanlpUtils.yinTou("abcdefg"));
        assertEquals("zhg", HanlpUtils.yinTou("中国"));
        assertEquals("zhhrmghg", HanlpUtils.yinTou("中华人民共和国"));
        assertEquals("pmx", HanlpUtils.yinTou("彭明喜"));
        assertEquals("wwy", HanlpUtils.yinTou("王文英"));
        assertEquals("xjp", HanlpUtils.yinTou("习近平"));
        assertEquals("chq", HanlpUtils.yinTou("重庆"));
        assertEquals("zhy", HanlpUtils.yinTou("重要"));
        assertEquals("zhy", HanlpUtils.yinTou("重123要"));
    }

    @Test
    public void testS2T() {
        String[][] str = {{"打印机", "打印機"}, {"以后", "以後"}, {"皇后", "皇后"}, {"买", "買"}, {"庆祝", "慶祝"},
                {"打印機", "打印機"}, {"abc", "abc"}, {"123", "123"}};
        for (String[] s : str) {
            assertEquals(s[1], HanlpUtils.s2t(s[0]));
        }
    }

    @Test
    public void testT2S() {
        String[][] str = {{"打印机", "打印機"}, {"以后", "以後"}, {"皇后", "皇后"}, {"买", "買"}, {"庆祝", "慶祝"},
                {"打印机", "打印机"}, {"abc", "abc"}, {"123", "123"}};
        for (String[] s : str) {
            assertEquals(s[0], HanlpUtils.t2s(s[1]));
        }
    }
}

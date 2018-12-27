package org.mx;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestClassUtils {
    @Test
    public void test() {
        List<String> list1 = ClassUtils.scanPackage("org.mx");
        List<String> list2 = ClassUtils.scanPackage("org.mx", true, true);
        assertEquals(list1, list2);
        assertTrue(list1.contains("org.mx.ClassUtils"));
        assertTrue(list1.contains("org.mx.FileUtils"));
        assertTrue(list1.contains("org.mx.TypeUtils"));
        assertTrue(list1.contains("org.mx.RandomUtils"));
        assertTrue(list1.contains("org.mx.config.SystemConfig"));
        assertTrue(list1.contains("org.mx.error.UserInterfaceError"));

        List<String> list3 = ClassUtils.scanPackage("org.mx", true, false);
        assertTrue(list3.contains("org.mx.ClassUtils"));
        assertTrue(list3.contains("org.mx.FileUtils"));
        assertTrue(list3.contains("org.mx.TypeUtils$Radix"));
        assertTrue(list3.contains("org.mx.RandomUtils"));
        assertTrue(list3.contains("org.mx.config.SystemConfig"));
        assertTrue(list3.contains("org.mx.rate.GeneralTopNRate$ValueType"));
        assertTrue(list3.contains("org.mx.rate.FloatTopNRate"));
        assertTrue(list3.contains("org.mx.error.UserInterfaceError"));

        List<String> list4 = ClassUtils.scanPackage("org.mx", false, true);
        assertTrue(list4.contains("org.mx.ClassUtils"));
        assertTrue(list4.contains("org.mx.FileUtils"));
        assertTrue(list4.contains("org.mx.TypeUtils"));
        assertTrue(list4.contains("org.mx.RandomUtils"));
        assertFalse(list4.contains("org.mx.config.SystemConfig"));
        assertFalse(list4.contains("org.mx.error.UserInterfaceError"));
    }
}

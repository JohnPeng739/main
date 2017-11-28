package org.mx;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestFileUtils {
    @Test
    public void testDeleteFile() {
        String parent = "testFileDelete";
        File file = new File(System.getProperty("user.dir"), parent);
        assertFalse(file.exists());
        File child1 = new File(file, "child1");
        assertFalse(child1.exists());
        File child2 = new File(file, "child2");
        assertFalse(child2.exists());
        File child11 = new File(child1, "child11");
        assertFalse(child11.exists());
        child11.mkdirs();
        assertTrue(file.exists());
        assertTrue(child1.exists());
        assertTrue(child11.exists());
        assertTrue(child11.isDirectory());
        child2.mkdir();
        assertTrue(child2.exists());
        assertTrue(child2.isDirectory());

        File file11 = new File(file, "file11.txt");
        assertFalse(file11.exists());
        try (FileOutputStream fio = new FileOutputStream(file11)) {
            fio.write("Test string line.".getBytes());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
        File file12 = new File(child11, "file12.txt");
        assertFalse(file12.exists());
        try {
            file12.createNewFile();
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
        assertTrue(file11.exists());
        assertTrue(file11.isFile());
        assertTrue(file12.exists());
        assertTrue(file12.isFile());

        try {
            FileUtils.deleteFile(file);
            assertFalse(file12.exists());
            assertFalse(file11.exists());
            assertFalse(child2.exists());
            assertFalse(child11.exists());
            assertFalse(child1.exists());
            assertFalse(file.exists());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }
}

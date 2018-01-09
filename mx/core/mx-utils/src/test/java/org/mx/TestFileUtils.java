package org.mx;

import javafx.animation.PathTransition;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TestFileUtils {
    @Test
    public void testDeleteFile() {
        String source = "testSource", target = "testTarget", child1 = "child1", child2 = "child2", child11 = "child11";
        String message = "Test string line.";
        Path pathSource = Paths.get(System.getProperty("user.dir"), source);
        Path pathTarget = Paths.get(System.getProperty("user.dir"), target);
        Path path1 = Paths.get(System.getProperty("user.dir"), source, child1);
        Path path2 = Paths.get(System.getProperty("user.dir"), source, child2);
        Path path11 = Paths.get(System.getProperty("user.dir"), source, child1,child11);
        assertFalse(Files.exists(pathSource));
        assertFalse(Files.exists(path1));
        assertFalse(Files.exists(path2));
        assertFalse(Files.exists(path11));
        assertFalse(Files.isDirectory(pathSource));
        assertFalse(Files.isDirectory(path1));
        assertFalse(Files.isDirectory(path2));
        assertFalse(Files.isDirectory(path11));
        try {
            Files.createDirectories(path11);
            Files.createDirectories(path2);
            assertTrue(Files.exists(pathSource));
            assertTrue(Files.exists(path1));
            assertTrue(Files.exists(path2));
            assertTrue(Files.exists(path11));

            String file11 = "file11.txt";
            Path pathFile11 = Paths.get(System.getProperty("user.dir"), source, file11);
            assertFalse(Files.exists(pathFile11));
            Files.createFile(pathFile11);
            assertTrue(Files.exists(pathFile11));
            assertTrue(Files.isRegularFile(pathFile11));
            Files.write(pathFile11, message.getBytes());
            assertEquals(message, new String(Files.readAllBytes(pathFile11)));

            String file12 = "file12.txt";
            Path pathFile12 = Paths.get(System.getProperty("user.dir"), source, child1, child11, file11);
            assertFalse(Files.exists(pathFile12));
            Files.createFile(pathFile12);
            assertTrue(Files.exists(pathFile12));
            assertTrue(Files.isRegularFile(pathFile12));
            Files.write(pathFile12, message.getBytes());
            assertEquals(message, new String(Files.readAllBytes(pathFile12)));

            FileUtils.copyFile(source, target);
            Path tpath1 = Paths.get(System.getProperty("user.dir"), target, child1);
            Path tpath2 = Paths.get(System.getProperty("user.dir"), target, child2);
            Path tpath11 = Paths.get(System.getProperty("user.dir"), target, child1,child11);
            Path tpathFile11 = Paths.get(System.getProperty("user.dir"), target, file11);
            Path tpathFile12 = Paths.get(System.getProperty("user.dir"), target, child1, child11, file11);
            assertTrue(Files.exists(pathTarget));
            assertTrue(Files.exists(tpath1));
            assertTrue(Files.exists(tpath2));
            assertTrue(Files.exists(tpath11));
            assertTrue(Files.exists(tpathFile11));
            assertTrue(Files.exists(tpathFile12));
            assertTrue(Files.isDirectory(pathTarget));
            assertTrue(Files.isDirectory(tpath1));
            assertTrue(Files.isDirectory(tpath2));
            assertTrue(Files.isDirectory(tpath11));
            assertTrue(Files.isRegularFile(tpathFile11));
            assertTrue(Files.isRegularFile(tpathFile12));
            assertEquals(message, new String(Files.readAllBytes(tpathFile11)));
            assertEquals(message, new String(Files.readAllBytes(tpathFile12)));

            FileUtils.deleteFile(target);
            assertFalse(Files.exists(pathTarget));
            assertFalse(Files.exists(tpath1));
            assertFalse(Files.exists(tpath2));
            assertFalse(Files.exists(tpath11));
            assertFalse(Files.exists(tpathFile11));
            assertFalse(Files.exists(tpathFile12));

            FileUtils.deleteFile(source);
            assertFalse(Files.exists(pathSource));
            assertFalse(Files.exists(path1));
            assertFalse(Files.exists(path2));
            assertFalse(Files.exists(path11));
            assertFalse(Files.exists(pathFile11));
            assertFalse(Files.exists(pathFile12));
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }
}

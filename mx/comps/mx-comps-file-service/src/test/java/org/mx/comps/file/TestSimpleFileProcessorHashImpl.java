package org.mx.comps.file;

import com.alibaba.fastjson.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.FileUtils;
import org.mx.StringUtils;
import org.mx.comps.file.config.FileServiceConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 简单实现的文件服务类的单元测试
 *
 * @author : john.peng created on date : 2017/12/04
 */
public class TestSimpleFileProcessorHashImpl {
    private AnnotationConfigApplicationContext context;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(FileServiceConfig.class);
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void testFileService() {
        FileWriteProcessor writeProcessor = context.getBean("simpleFilePersistProcessor", FileWriteProcessor.class);
        assertNotNull(writeProcessor);
        assertFalse(writeProcessor.isOpened());
        Map<String, Object> map = new HashMap<>();
        map.put("command", "start");
        String directory = "/test1/test2/", filename = "filename.txt", msg = "This is test message.";
        map.put("directory", directory);
        map.put("filename", filename);
        JSONObject json = new JSONObject(map);
        boolean hashed = context.getEnvironment().getProperty("file.simple.hashed", Boolean.class, true);

        File file = new File(directory, filename);
        FileServiceListener listener = new FileServiceListener() {
            @Override
            public void started(FileServiceDescriptor descriptor) {
                assertNotNull(descriptor);
                assertFalse(StringUtils.isBlank(descriptor.getId()));
                if (!hashed) {
                    assertEquals(file.getAbsolutePath(), descriptor.getPath());
                }
            }

            @Override
            public void finished(FileServiceDescriptor descriptor) {
                assertNotNull(descriptor);
                assertFalse(StringUtils.isBlank(descriptor.getId()));
                if (!hashed) {
                    assertEquals(file.getAbsolutePath(), descriptor.getPath());
                }
            }

            @Override
            public void canceled(FileServiceDescriptor descriptor) {
                assertNotNull(descriptor);
                assertFalse(StringUtils.isBlank(descriptor.getId()));
                if (!hashed) {
                    assertEquals(file.getAbsolutePath(), descriptor.getPath());
                }
            }
        };
        writeProcessor.setFileServiceListener(listener);

        writeProcessor.command(json);
        assertTrue(writeProcessor.isOpened());
        if (!hashed) {
            assertEquals(filename, writeProcessor.getFileServiceDescriptor().getFilename());
            assertEquals(file.getAbsolutePath(), writeProcessor.getFileServiceDescriptor().getPath());
        }
        writeProcessor.write(new ByteArrayInputStream(msg.getBytes()));
        assertTrue(writeProcessor.isOpened());
        writeProcessor.close();
        assertFalse(writeProcessor.isOpened());

        try {
            FileReadProcessor readProcessor = context.getBean("simpleFilePersistProcessor", FileReadProcessor.class);
            writeProcessor.setFileServiceListener(listener);
            assertNotNull(readProcessor);
            assertFalse(readProcessor.isOpened());
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setCharacterEncoding("UTF-8");
            req.addParameter("directory", directory);
            req.addParameter("filename", filename);
            readProcessor.init(req);
            assertTrue(readProcessor.isOpened());
            assertEquals(msg.getBytes().length, readProcessor.getFileServiceDescriptor().getLength());
            if (!hashed) {
                assertEquals(filename, readProcessor.getFileServiceDescriptor().getFilename());
                assertEquals(file.getAbsolutePath(), readProcessor.getFileServiceDescriptor().getPath());
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(100);
            readProcessor.read(out);
            assertEquals(msg, new String(out.toString()));
            readProcessor.close();
            assertFalse(readProcessor.isOpened());

            FileUtils.deleteFile(new File(System.getProperty("user.dir"), "test1"));
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}

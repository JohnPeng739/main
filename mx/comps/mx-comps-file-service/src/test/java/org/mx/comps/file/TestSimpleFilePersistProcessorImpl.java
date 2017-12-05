package org.mx.comps.file;

import com.alibaba.fastjson.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.FileUtils;
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
public class TestSimpleFilePersistProcessorImpl {
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
        FilePersistProcessor processor = context.getBean("simpleFilePersistProcessor", FilePersistProcessor.class);
        assertNotNull(processor);
        assertFalse(processor.isOpened());
        Map<String, Object> map = new HashMap<>();
        map.put("command", "start");
        String directory = "/test1/test2/", filename = "filename.txt", msg = "This is test message.";
        map.put("directory", directory);
        map.put("filename", filename);
        JSONObject json = new JSONObject(map);
        processor.command(json.toJSONString());
        assertTrue(processor.isOpened());
        assertEquals(filename, processor.getFilename());
        File file = new File(directory, filename);
        assertEquals(file.getAbsolutePath(), ((FileManageDescriptor) processor).getPath());
        processor.persist(new ByteArrayInputStream(msg.getBytes()));
        assertTrue(processor.isOpened());
        processor.close();
        assertFalse(processor.isOpened());

        try {
            MockHttpServletRequest req = new MockHttpServletRequest();
            req.setCharacterEncoding("UTF-8");
            req.addParameter("directory", directory);
            req.addParameter("filename", filename);
            processor.init(req);
            assertTrue(processor.isOpened());
            assertEquals(msg.getBytes().length, processor.getLength());
            assertEquals(filename, processor.getFilename());
            assertEquals(file.getAbsolutePath(), ((FileManageDescriptor) processor).getPath());
            ByteArrayOutputStream out = new ByteArrayOutputStream(100);
            processor.read(out);
            assertEquals(msg, new String(out.toString()));
            processor.close();
            assertFalse(processor.isOpened());

            FileUtils.deleteFile(new File(System.getProperty("user.dir"), "test1"));
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }
}

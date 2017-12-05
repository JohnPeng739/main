package org.mx.comps.file;

import com.alibaba.fastjson.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.FileUtils;
import org.mx.comps.file.config.FileServiceConfig;
import org.mx.comps.file.servlet.FileDownloadServlet;
import org.mx.comps.file.websocket.FileUploadWebsocket;
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
public class TestBeans {
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
        FileUploadWebsocket websocket = context.getBean("fileUploadWebsocket", FileUploadWebsocket.class);
        assertNotNull(websocket);
        FileDownloadServlet servlet = context.getBean("fileDownloadServlet", FileDownloadServlet.class);
        assertNotNull(servlet);
    }
}

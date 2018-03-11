package org.mx.comps.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.eclipse.jetty.server.Server;
import org.java_websocket.WebSocket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.FileUtils;
import org.mx.comps.file.config.FileServiceConfig;
import org.mx.comps.file.servlet.FileDownloadServlet;
import org.mx.comps.file.websocket.FileUploadWebsocket;
import org.mx.service.client.websocket.BaseWebsocketClientListener;
import org.mx.service.client.websocket.WsClientInvoke;
import org.mx.service.server.AbstractServerFactory;
import org.mx.service.server.ServletServerFactory;
import org.mx.service.server.WebsocketServerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 简单实现的文件服务类的单元测试
 *
 * @author : john.peng created on date : 2017/12/04
 */
public class TestServers {
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

    private void testServlet() throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();

        // by GET
        HttpGet get = new HttpGet(String.format("http://localhost:9998/download?directory=%s&filename=%s",
                TestProcessorSimple.directory, TestProcessorSimple.filename));
        CloseableHttpResponse res = client.execute(get);
        assertEquals(200, res.getStatusLine().getStatusCode());
        assertNotNull(res.getEntity());
        assertEquals(TestProcessorSimple.msg.length(), res.getEntity().getContentLength());
        assertNotNull(res.getEntity().getContent());
        BufferedReader reader = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        assertNotNull(reader);
        assertEquals(TestProcessorSimple.msg, reader.readLine());
        reader.close();
        res.close();

        // by POST
        HttpPost post = new HttpPost("http://localhost:9998/download");
        post.addHeader(HTTP.CONTENT_TYPE, "application/json");
        Map<String, Object> map = new HashMap<>();
        map.put("directory", TestProcessorSimple.directory);
        map.put("filename", TestProcessorSimple.filename);
        JSONObject json = new JSONObject(map);
        StringEntity se = new StringEntity(json.toJSONString());
        se.setContentType("text/json");
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        post.setEntity(se);
        res = client.execute(post);
        assertEquals(200, res.getStatusLine().getStatusCode());
        assertNotNull(res.getEntity());
        assertEquals(TestProcessorSimple.msg.length(), res.getEntity().getContentLength());
        assertNotNull(res.getEntity().getContent());
        reader = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        assertNotNull(reader);
        assertEquals(TestProcessorSimple.msg, reader.readLine());
        reader.close();
        res.close();
        client.close();
    }

    @Test
    public void testFileDownloadServlet() {
        FileDownloadServlet servlet = context.getBean("fileDownloadServlet", FileDownloadServlet.class);
        assertNotNull(servlet);
        AbstractServerFactory factory = context.getBean(ServletServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        TestProcessorSimple.testFileSave(context);

        try {
            testServlet();

            FileUtils.deleteFile(Paths.get(System.getProperty("user.dir"), "data"));
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    private JSONObject createInitCmd() {
        JSONObject json = new JSONObject();
        json.put("command", "init");
        json.put("processorType", "simple");
        return json;
    }

    private JSONObject createStartCmd() {
        JSONObject json = new JSONObject();
        json.put("command", "start");
        json.put("directory", TestProcessorSimple.directory);
        json.put("filename", TestProcessorSimple.filename);
        json.put("offset", 0);
        return json;
    }

    private JSONObject createFinishCmd() {
        JSONObject json = new JSONObject();
        json.put("command", "finish");
        return json;
    }

    private JSONObject createCancelCmd() {
        JSONObject json = new JSONObject();
        json.put("command", "cancel");
        return json;
    }

    @Test
    public void testFileUploadWebsocket() {
        FileUploadWebsocket websocket = context.getBean("fileUploadWebsocket", FileUploadWebsocket.class);
        assertNotNull(websocket);
        AbstractServerFactory factory = context.getBean(WebsocketServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        try {
            // test websocket client
            TestWebsocketListener listener = new TestWebsocketListener();
            WsClientInvoke invoke = new WsClientInvoke();
            invoke.init("ws://localhost:9997/wsupload", listener);
            Thread.sleep(1000);
            assertEquals(WebSocket.READYSTATE.OPEN, invoke.getState());
            invoke.send(createInitCmd().toJSONString());
            Thread.sleep(1000);
            String textMsg = listener.textMsg;
            JSONObject json = JSON.parseObject(textMsg);
            assertEquals("init", json.getString("command"));
            assertEquals("simple", json.getString("processorType"));
            assertEquals("ok", json.getString("result"));
            invoke.send(createStartCmd().toJSONString());
            Thread.sleep(1000);
            textMsg = listener.textMsg;
            json = JSON.parseObject(textMsg);
            assertEquals("start", json.getString("command"));
            assertEquals(TestProcessorSimple.directory, json.getString("directory"));
            assertEquals(TestProcessorSimple.filename, json.getString("filename"));
            assertEquals(0, json.getIntValue("offset"));
            assertEquals("ok", json.getString("result"));
            invoke.send(TestProcessorSimple.msg.getBytes());
            Thread.sleep(1000);

            invoke.send(createCancelCmd().toJSONString());
            Thread.sleep(1000);
            textMsg = listener.textMsg;
            json = JSON.parseObject(textMsg);
            assertEquals("cancel", json.getString("command"));
            assertEquals("ok", json.getString("result"));
            invoke.send(createFinishCmd().toJSONString());
            Thread.sleep(1000);
            // 这里后台应该抛出会话被关闭的异常，前端收到error消息。
            textMsg = listener.textMsg;
            json = JSON.parseObject(textMsg);
            assertEquals("finish", json.getString("command"));
            assertEquals("error", json.getString("result"));
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet get = new HttpGet(String.format("http://localhost:9998/download?directory=%s&filename=%s",
                    TestProcessorSimple.directory, TestProcessorSimple.filename));
            CloseableHttpResponse res = httpClient.execute(get);
            assertEquals(404, res.getStatusLine().getStatusCode());
            res.close();
            httpClient.close();

            invoke.send(createStartCmd().toJSONString());
            Thread.sleep(1000);
            textMsg = listener.textMsg;
            json = JSON.parseObject(textMsg);
            assertEquals("start", json.getString("command"));
            assertEquals(TestProcessorSimple.directory, json.getString("directory"));
            assertEquals(TestProcessorSimple.filename, json.getString("filename"));
            assertEquals(0, json.getIntValue("offset"));
            assertEquals("ok", json.getString("result"));
            invoke.send(TestProcessorSimple.msg.getBytes());
            Thread.sleep(1000);

            invoke.send(createFinishCmd().toJSONString());
            Thread.sleep(1000);
            textMsg = listener.textMsg;
            json = JSON.parseObject(textMsg);
            assertEquals("finish", json.getString("command"));
            assertEquals("ok", json.getString("result"));
            invoke.close();
            Thread.sleep(1000);
            assertEquals(WebSocket.READYSTATE.CLOSED, invoke.getState());
            invoke.close();

            testServlet();
            FileUtils.deleteFile(Paths.get(System.getProperty("user.dir"), "data"));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    private class TestWebsocketListener extends BaseWebsocketClientListener {
        private String textMsg;
        private byte[] binaryMsg;

        @Override
        public void onOpen() {
            //
        }

        @Override
        public void onBinaryMessage(byte[] bytes) {
            this.binaryMsg = bytes;
        }

        @Override
        public void onTextMessage(String message) {
            this.textMsg = message;
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            //
        }

        @Override
        public void onError(Exception ex) {
            //
        }
    }
}

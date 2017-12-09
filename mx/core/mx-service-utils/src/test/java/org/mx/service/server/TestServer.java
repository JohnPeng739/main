package org.mx.service.server;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.rest.client.RestClientInvoke;
import org.mx.service.rest.client.RestInvokeException;
import org.mx.service.server.config.TestConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/**
 * Created by john on 2017/11/4.
 */
public class TestServer {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestConfig.class);
    }

    @After
    public void after() {
        context.close();
    }

    @Test
    public void testHttpServer() {
        AbstractServerFactory factory = context.getBean(HttpServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        // test service client
        try {
            RestClientInvoke invoke = new RestClientInvoke();
            String str = invoke.get("http://localhost:9999/service/get", String.class);
            assertNotNull(str);
            assertEquals("match the response data.", "get data.", str);

            String data = "hello world";
            str = invoke.post("http://localhost:9999/service/post", data, String.class);
            assertNotNull(str);
            assertEquals("match the response data.", String.format("post data: %s.", data), str);

            data = "hello world";
            str = invoke.put("http://localhost:9999/service/put", data, String.class);
            assertNotNull(str);
            assertEquals("match the response data.", String.format("put data: %s.", data), str);

            invoke.close();
        } catch (RestInvokeException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testServletServer() {
        AbstractServerFactory factory = context.getBean(ServletServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet("http://localhost:9998/download?id=123");
            CloseableHttpResponse res = client.execute(get);
            assertEquals(200, res.getStatusLine().getStatusCode());
            String msg = "This is a test message file.";
            assertNotNull(res.getEntity());
            assertEquals(msg.length(), res.getEntity().getContentLength());
            assertNotNull(res.getEntity().getContent());
            BufferedReader reader = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            assertNotNull(reader);
            assertEquals(msg, reader.readLine());
            reader.close();
            res.close();
            client.close();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testWebsocketServer() {
        AbstractServerFactory factory = context.getBean(WebsocketServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        try {
            // test websocket client
            TestWebsocketClient client = new TestWebsocketClient(new URI("ws://localhost:9997/echo"));
            client.connect();
            Thread.sleep(1000);
            assertEquals(WebSocket.READYSTATE.OPEN, client.getReadyState());
            assertEquals("Server is ok.", client.textMsg);
            String msg = "hello, john";
            client.send(msg);
            Thread.sleep(1000);
            assertEquals(String.format("Server echo: %s.", msg), client.textMsg);
            client.send(msg.getBytes());
            Thread.sleep(1000);
            assertEquals(msg, new String(client.binaryMsg));
            client.close();
            Thread.sleep(1000);
            assertEquals(WebSocket.READYSTATE.CLOSED, client.getReadyState());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    private class TestWebsocketClient extends WebSocketClient {
        private String textMsg;
        private byte[] binaryMsg;

        public TestWebsocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            //
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            this.binaryMsg = bytes.array();
        }

        @Override
        public void onMessage(String message) {
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

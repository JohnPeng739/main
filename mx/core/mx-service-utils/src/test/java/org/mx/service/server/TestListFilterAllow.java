package org.mx.service.server;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.java_websocket.WebSocket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.rest.client.RestClientInvoke;
import org.mx.service.rest.client.RestInvokeException;
import org.mx.service.server.config.TestConfig;
import org.mx.service.server.config.TestListFilterAllowConfig;
import org.mx.service.ws.client.BaseWebsocketClientListener;
import org.mx.service.ws.client.WsClientInvoke;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * Created by john on 2017/11/4.
 */
public class TestListFilterAllow {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestListFilterAllowConfig.class);
    }

    @After
    public void after() {
        context.close();
    }

    @Test
    public void testAnyConnectWebsocket() {
        AbstractServerFactory factory = context.getBean(WebsocketServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        try {
            // test websocket client
            WsClientInvoke invoke1 = new WsClientInvoke();
            TestWebsocketListener listener = new TestWebsocketListener();
            invoke1.init("ws://localhost:9997/echo", listener);
            Thread.sleep(1000);
            assertEquals(WebSocket.READYSTATE.OPEN, invoke1.getState());
            assertEquals("Server is ok.", listener.textMsg);
            String msg = "hello, john";
            invoke1.send(msg);
            Thread.sleep(1000);
            assertEquals(String.format("Server echo: %s.", msg), listener.textMsg);
            invoke1.close();
            Thread.sleep(1000);
            assertEquals(WebSocket.READYSTATE.CLOSED, invoke1.getState());
        } catch (Exception ex) {
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

package org.mx.service.test.server;

import org.eclipse.jetty.server.Server;
import org.java_websocket.WebSocket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.server.AbstractServerFactory;
import org.mx.service.server.WebsocketServerFactory;
import org.mx.service.test.server.config.TestListFilterBlockConfig;
import org.mx.service.client.websocket.BaseWebsocketClientListener;
import org.mx.service.client.websocket.WsClientInvoke;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

/**
 * Created by john on 2017/11/4.
 */
public class TestWebsocketServerListFilterBlock {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestListFilterBlockConfig.class);
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
            invoke1.init(String.format("ws://localhost:%d/echo",
                    context.getEnvironment().getProperty("websocket.port", Integer.class, 9997)),
                    listener, false);
            Thread.sleep(1000);
            assertEquals(WebSocket.READYSTATE.CLOSED, invoke1.getState());
            invoke1.close();
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

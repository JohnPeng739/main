package org.mx.service.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.client.websocket.DefaultWebsocketClientMoniter;
import org.mx.service.client.websocket.WsClientInvoke;
import org.mx.service.server.AbstractServerFactory;
import org.mx.service.server.WebsocketServerFactory;
import org.mx.service.test.config.TestListFilterBlockConfig;
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
            TestWebsocketMoniter listener = new TestWebsocketMoniter();
            invoke1.init(String.format("ws://localhost:%d/test",
                    context.getEnvironment().getProperty("websocket.port", Integer.class, 9997)),
                    listener, false);
            Thread.sleep(1000);
            assertFalse(invoke1.isReady());
            invoke1.close();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    private class TestWebsocketMoniter extends DefaultWebsocketClientMoniter {
        private String textMsg;
        private byte[] binaryMsg;

        @Override
        public void onBinaryMessage(Session session, byte[] bytes, int offset, int length) {
            this.binaryMsg = bytes;
        }

        @Override
        public void onTextMessage(Session session, String message) {
            this.textMsg = message;
        }
    }
}

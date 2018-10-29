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
import org.mx.service.test.config.TestListFilterAllowConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.*;

/**
 * Created by john on 2017/11/4.
 */
public class TestWebsocketServerListFilterAllow {
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
            TestWebsocketMoniter listener = new TestWebsocketMoniter();
            invoke1.init(String.format("ws://localhost:%d/test",
                    context.getEnvironment().getProperty("websocket.port", Integer.class, 9997)),
                    listener, false);
            Thread.sleep(1000);
            assertTrue(invoke1.isReady());
            assertThat(listener.textMsg, startsWith("Server is ok:"));
            String msg = "hello, john";
            invoke1.send(msg);
            Thread.sleep(1000);
            assertEquals(String.format("Server echo: %s.", msg), listener.textMsg);
            invoke1.close();
            Thread.sleep(1000);
            assertFalse(invoke1.isReady());
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

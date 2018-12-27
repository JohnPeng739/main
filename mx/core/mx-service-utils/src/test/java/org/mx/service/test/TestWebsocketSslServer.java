package org.mx.service.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.client.websocket.DefaultWebsocketClientMoniter;
import org.mx.service.client.websocket.WsClientInvoke;
import org.mx.service.server.AbstractServerFactory;
import org.mx.service.server.WebsocketServerConfigBean;
import org.mx.service.server.WebsocketServerFactory;
import org.mx.service.test.config.TestWebsocketSslConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.*;

public class TestWebsocketSslServer {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestWebsocketSslConfig.class);
    }

    @After
    public void after() {
        context.close();
    }

    @Test
    public void testWebsocketServer() {
        AbstractServerFactory factory = context.getBean(WebsocketServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);
        WebsocketServerConfigBean config = context.getBean(WebsocketServerConfigBean.class);
        assertNotNull(config);

        // test websocket client
        WsClientInvoke invoke = new WsClientInvoke();
        TestWebsocketMoniter listener = new TestWebsocketMoniter();
        try {
            invoke.init(String.format("wss://localhost:%d/test", config.getPort()), listener, false,
                    config.getKeystorePath(), config.getKeystorePassword(), config.getKeyManagerPassword());
            Thread.sleep(1000);
            assertTrue(invoke.isReady());
            assertThat(listener.textMsg, startsWith("Server is ok:"));
            String msg = "hello, john";
            invoke.send(msg);
            Thread.sleep(1000);
            assertEquals(String.format("Server echo: %s.", msg), listener.textMsg);
            invoke.send(msg.getBytes());
            Thread.sleep(1000);
            assertEquals(msg, new String(listener.binaryMsg));
            invoke.close();
            Thread.sleep(1000);
            assertFalse(invoke.isReady());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
        // 测试手动关闭后无法重连
        try {
            invoke.send("test message");
            fail("Here need a exception.");
        } catch (Exception ex) {
            // do nothing
        }
        invoke.close();
    }

    @Test
    public void testWebsocketServerReconnect() {
        AbstractServerFactory factory = context.getBean(WebsocketServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);
        WebsocketServerConfigBean config = context.getBean(WebsocketServerConfigBean.class);
        assertNotNull(config);

        // test websocket client
        WsClientInvoke invoke = new WsClientInvoke();
        TestWebsocketMoniter listener = new TestWebsocketMoniter();
        try {
            invoke.init(String.format("wss://localhost:%d/test", config.getPort()), listener, true,
                    config.getKeystorePath(), config.getKeystorePassword(), config.getKeyManagerPassword());
            Thread.sleep(1000);
            assertTrue(invoke.isReady());
            assertThat(listener.textMsg, startsWith("Server is ok:"));
            // 等待直到服务器关闭连接
            Thread.sleep(30000);

            // 测试重连
            String msg = "hello, john";
            invoke.send(msg);
            Thread.sleep(1000);
            assertEquals(String.format("Server echo: %s.", msg), listener.textMsg);
            invoke.send(msg.getBytes());
            Thread.sleep(1000);
            assertEquals(msg, new String(listener.binaryMsg));
            invoke.close();
            Thread.sleep(1000);
            assertFalse(invoke.isReady());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        invoke.close();
    }

    @Test
    public void testAnyConnectWebsocket() {
        AbstractServerFactory factory = context.getBean(WebsocketServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);
        WebsocketServerConfigBean config = context.getBean(WebsocketServerConfigBean.class);
        assertNotNull(config);

        try {
            // test websocket client
            WsClientInvoke invoke1 = new WsClientInvoke();
            WsClientInvoke invoke2 = new WsClientInvoke();
            TestWebsocketMoniter listener = new TestWebsocketMoniter();
            invoke1.init(String.format("wss://localhost:%d/test", config.getPort()), listener, false,
                    config.getKeystorePath(), config.getKeystorePassword(), config.getKeyManagerPassword());
            invoke2.init(String.format("wss://localhost:%d/echo", config.getPort()), listener, false,
                    config.getKeystorePath(), config.getKeystorePassword(), config.getKeyManagerPassword());
            Thread.sleep(1000);
            assertTrue(invoke1.isReady());
            assertTrue(invoke2.isReady());
            assertThat(listener.textMsg, startsWith("Server is ok:"));
            String msg = "hello, john";
            invoke1.send(msg);
            invoke2.send(msg);
            Thread.sleep(1000);
            assertEquals(String.format("Server echo: %s.", msg), listener.textMsg);
            invoke1.close();
            invoke2.close();
            Thread.sleep(1000);
            assertFalse(invoke1.isReady());
            assertFalse(invoke2.isReady());
            invoke1.close();
            invoke2.close();
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

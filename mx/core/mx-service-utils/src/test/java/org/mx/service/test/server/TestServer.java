package org.mx.service.test.server;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.java_websocket.WebSocket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.StringUtils;
import org.mx.service.client.comm.CommClientInvoke;
import org.mx.service.client.rest.RestClientInvoke;
import org.mx.service.client.rest.RestInvokeException;
import org.mx.service.client.websocket.BaseWebsocketClientListener;
import org.mx.service.client.websocket.WsClientInvoke;
import org.mx.service.rest.vo.DataVO;
import org.mx.service.server.*;
import org.mx.service.server.comm.CommServiceProvider;
import org.mx.service.server.comm.DefaultPacketWrapper;
import org.mx.service.server.comm.TcpCommServiceProvider;
import org.mx.service.server.comm.UdpCommServiceProvider;
import org.mx.service.test.server.comm.TestTcpReceiver;
import org.mx.service.test.server.comm.TestUdpReceiver;
import org.mx.service.test.server.config.TestConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.*;

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
    @SuppressWarnings("unchecked")
    public void testHttpServer() {
        AbstractServerFactory factory = context.getBean(HttpServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        // test service client
        try {
            RestClientInvoke invoke = new RestClientInvoke();
            DataVO<String> str = invoke.get("http://localhost:9999/service/get", DataVO.class);
            assertNotNull(str);
            assertEquals("match the response data.", "get data.", str.getData());

            String data = StringUtils.repeat(65535000, "a");
            System.out.println(data.length());
            str = invoke.post("http://192.168.0.248:9999/service/post", data, DataVO.class);
            assertNotNull(str);
            System.out.println(str.getData().length());
            assertEquals("match the response data.", String.format("post data: %s.", data), str.getData());

            data = "hello world";
            str = invoke.put("http://localhost:9999/service/put", data, DataVO.class);
            assertNotNull(str);
            assertEquals("match the response data.", String.format("put data: %s.", data), str.getData());

            invoke.close();
        } catch (RestInvokeException ex) {
            ex.printStackTrace();
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
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    public void testWebsocketServer() {
        AbstractServerFactory factory = context.getBean(WebsocketServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        // test websocket client
        WsClientInvoke invoke = new WsClientInvoke();
        TestWebsocketListener listener = new TestWebsocketListener();
        try {
            invoke.init("ws://localhost:9997/echo", listener, false);
            Thread.sleep(3000);
            assertEquals(WebSocket.READYSTATE.OPEN, invoke.getState());
            assertThat(listener.textMsg, startsWith("Server is ok:"));
            String msg = "hello, john";
            invoke.send(msg);
            Thread.sleep(3000);
            assertEquals(String.format("Server echo: %s.", msg), listener.textMsg);
            invoke.send(msg.getBytes());
            Thread.sleep(3000);
            assertEquals(msg, new String(listener.binaryMsg));
            invoke.close();
            Thread.sleep(3000);
            assertEquals(WebSocket.READYSTATE.CLOSED, invoke.getState());
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
    }

    @Test
    public void testWebsocketServerReconnect() {
        AbstractServerFactory factory = context.getBean(WebsocketServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        // test websocket client
        WsClientInvoke invoke = new WsClientInvoke();
        TestWebsocketListener listener = new TestWebsocketListener();
        try {
            invoke.init("ws://localhost:9997/echo", listener, true);
            Thread.sleep(1000);
            assertEquals(WebSocket.READYSTATE.OPEN, invoke.getState());
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
            assertEquals(WebSocket.READYSTATE.CLOSED, invoke.getState());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
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
            WsClientInvoke invoke2 = new WsClientInvoke();
            TestWebsocketListener listener = new TestWebsocketListener();
            invoke1.init("ws://localhost:9997/echo", listener);
            invoke2.init("ws://localhost:9997/echo", listener);
            Thread.sleep(1000);
            assertEquals(WebSocket.READYSTATE.OPEN, invoke1.getState());
            assertEquals(WebSocket.READYSTATE.OPEN, invoke2.getState());
            assertThat(listener.textMsg, startsWith("Server is ok:"));
            String msg = "hello, john";
            invoke1.send(msg);
            invoke2.send(msg);
            Thread.sleep(1000);
            assertEquals(String.format("Server echo: %s.", msg), listener.textMsg);
            invoke1.close();
            invoke2.close();
            Thread.sleep(1000);
            assertEquals(WebSocket.READYSTATE.CLOSED, invoke1.getState());
            assertEquals(WebSocket.READYSTATE.CLOSED, invoke2.getState());
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testTcpCommServer() {
        CommServerFactory factory = context.getBean(CommServerFactory.class);
        assertNotNull(factory);
        TcpCommServiceProvider provider = factory.getTcpProvider(9996);
        assertNotNull(provider);
        TestTcpReceiver tcpReceiver = context.getBean("tcpReceiver", TestTcpReceiver.class);
        assertNotNull(tcpReceiver);
        Integer port = context.getEnvironment().getProperty("tcp.port", Integer.class);
        assertNotNull(port);
        Integer length = context.getEnvironment().getProperty("udp.maxLength", Integer.class);
        Integer timeout = context.getEnvironment().getProperty("udp.maxTimeout", Integer.class);

        CommClientInvoke client = new CommClientInvoke(CommServiceProvider.CommServiceType.TCP, tcpReceiver,
                new DefaultPacketWrapper(), "127.0.0.1", port, length, timeout);

        try {
            byte[] payload = "test message".getBytes();
            client.send(payload);
            Thread.sleep(20);
            assertNotNull(tcpReceiver.getPayload());
            assertArrayEquals(payload, tcpReceiver.getPayload());

            // 在默认的包装器下，最大数据载荷为：length - 12
            payload = "12345678901234567890123456789012345678".getBytes();
            client.send(payload);
            Thread.sleep(20);
            assertNotNull(tcpReceiver.getPayload());
            assertArrayEquals(payload, tcpReceiver.getPayload());
            // TODO
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    @Test
    public void testUdpCommServer() {
        CommServerFactory factory = context.getBean(CommServerFactory.class);
        assertNotNull(factory);
        UdpCommServiceProvider provider = factory.getUdpProvider(9995);
        assertNotNull(provider);
        TestUdpReceiver udpReceiver = context.getBean("udpReceiver", TestUdpReceiver.class);
        assertNotNull(udpReceiver);
        Integer port = context.getEnvironment().getProperty("udp.servers.1.port", Integer.class);
        Integer length = context.getEnvironment().getProperty("udp.servers.1.maxLength", Integer.class);
        Integer timeout = context.getEnvironment().getProperty("udp.servers.1.maxTimeout", Integer.class);
        assertNotNull(port);

        CommClientInvoke client = new CommClientInvoke(CommServiceProvider.CommServiceType.UDP, udpReceiver,
                new DefaultPacketWrapper(), "127.0.0.1", 3000, length, timeout);

        try {
            byte[] payload = "test message".getBytes();
            client.send(payload);
            Thread.sleep(50);
            assertNotNull(udpReceiver.getPayload());
            assertArrayEquals(payload, udpReceiver.getPayload());

            // 在默认的包装器下，最大数据载荷为：length - 12
            payload = "12345678901234567890123456789012345678".getBytes();
            client.send(payload);
            Thread.sleep(50);
            assertNotNull(udpReceiver.getPayload());
            assertArrayEquals(payload, udpReceiver.getPayload());

            try {
                payload = "1234567890123456789012345678901234567890".getBytes();
                client.send(payload);
                fail("here need exception.");
            } catch (Exception ex) {
                // success
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
        client.close();
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

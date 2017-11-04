package org.mx.rest.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.rest.client.RestClientInvoke;
import org.mx.rest.client.RestInvokeException;
import org.mx.rest.server.config.TestConfig;
import org.mx.rest.server.websocket.EchoSocket;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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

        // test rest client
        try {
            RestClientInvoke invoke = new RestClientInvoke();
            String str = invoke.get("http://localhost:9999/rest/get", String.class);
            assertNotNull(str);
            assertEquals("match the response data.", "get data.", str);

            String data = "hello world";
            str = invoke.post("http://localhost:9999/rest/post", data, String.class);
            assertNotNull(str);
            assertEquals("match the response data.", String.format("post data: %s.", data), str);

            data = "hello world";
            str = invoke.put("http://localhost:9999/rest/put", data, String.class);
            assertNotNull(str);
            assertEquals("match the response data.", String.format("put data: %s.", data), str);

            invoke.close();
        } catch (RestInvokeException ex) {
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
        WebSocketClient client = new WebSocketClient();
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        EchoSocket socket = new EchoSocket();
        try {
            client.start();
            client.connect(socket, new URI("ws://localhost:9997/upload"), request);
            socket.awaitClose(60, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
        try {
            client.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

package org.mx.service.test.server;

import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.StringUtils;
import org.mx.service.client.rest.RestClientInvoke;
import org.mx.service.client.rest.RestInvokeException;
import org.mx.service.rest.vo.DataVO;
import org.mx.service.server.AbstractServerFactory;
import org.mx.service.server.HttpServerFactory;
import org.mx.service.test.server.config.TestHttpConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class TestHttpServer {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestHttpConfig.class);
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

            String data = StringUtils.repeat(65535, "a");
            str = invoke.post("http://localhost:9999/service/post", data, DataVO.class);
            assertNotNull(str);
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
}

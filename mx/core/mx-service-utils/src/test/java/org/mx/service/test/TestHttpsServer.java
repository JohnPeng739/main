package org.mx.service.test;

import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.client.rest.RestClientInvoke;
import org.mx.service.client.rest.RestInvokeException;
import org.mx.service.error.UserInterfaceServiceErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.service.server.AbstractServerFactory;
import org.mx.service.server.RestfulServerFactory;
import org.mx.service.test.config.TestHttpsConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class TestHttpsServer {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestHttpsConfig.class);
    }

    @After
    public void after() {
        context.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHttpsServer() {
        AbstractServerFactory factory = context.getBean(RestfulServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        // test service client
        try {
            String keystorePath = context.getEnvironment().getProperty("restful.security.keystore", "./keystore");
            RestClientInvoke invoke = new RestClientInvoke(keystorePath);
            DataVO<String> dataVO = invoke.get("https://localhost:9999/service/get", DataVO.class);
            assertNotNull(dataVO);
            assertEquals("match the response data.", "get data.", dataVO.getData());

            String data = StringUtils.repeat(65535, "a");
            dataVO = invoke.post("https://localhost:9999/service/post", data, DataVO.class);
            assertNotNull(dataVO);
            assertEquals("match the response data.", String.format("post data: %s.", data), dataVO.getData());

            data = "hello world";
            dataVO = invoke.put("https://localhost:9999/service/put", data, DataVO.class);
            assertNotNull(dataVO);
            assertEquals("match the response data.", String.format("put data: %s.", data), dataVO.getData());

            dataVO = invoke.get("https://localhost:9999/service/exception/system", DataVO.class);
            assertNotNull(dataVO);
            assertEquals(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM.getErrorCode(),
                    dataVO.getErrorCode());
            assertEquals(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM.getErrorMessage(),
                    dataVO.getErrorMessage());

            dataVO = invoke.get("https://localhost:9999/service/exception", DataVO.class);
            assertNotNull(dataVO);
            assertEquals(UserInterfaceServiceErrorException.ServiceErrors.SERVICE_OTHER_FAIL.getErrorCode(),
                    dataVO.getErrorCode());
            assertEquals(UserInterfaceServiceErrorException.ServiceErrors.SERVICE_OTHER_FAIL.getErrorMessage(),
                    dataVO.getErrorMessage());

            invoke.close();
        } catch (RestInvokeException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}

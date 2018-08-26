package org.mx.service.test;

import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.client.rest.RestClientInvoke;
import org.mx.service.client.rest.RestInvokeException;
import org.mx.service.rest.UserInterfaceExceptionMapper;
import org.mx.service.rest.vo.DataVO;
import org.mx.service.server.AbstractServerFactory;
import org.mx.service.server.RestfulServerFactory;
import org.mx.service.test.config.TestHttpConfig;
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
        AbstractServerFactory factory = context.getBean(RestfulServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        // test service client
        try {
            RestClientInvoke invoke = new RestClientInvoke();
            DataVO<String> dataVO = invoke.get("http://localhost:9999/service/get", DataVO.class);
            assertNotNull(dataVO);
            assertEquals("match the response data.", "get data.", dataVO.getData());

            String data = StringUtils.repeat(65535, "a");
            dataVO = invoke.post("http://localhost:9999/service/post", data, DataVO.class);
            assertNotNull(dataVO);
            assertEquals("match the response data.", String.format("post data: %s.", data), dataVO.getData());

            data = "hello world";
            dataVO = invoke.put("http://localhost:9999/service/put", data, DataVO.class);
            assertNotNull(dataVO);
            assertEquals("match the response data.", String.format("put data: %s.", data), dataVO.getData());

            dataVO = invoke.get("http://localhost:9999/service/exception/system", DataVO.class);
            assertNotNull(dataVO);
            assertEquals(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM.getErrorCode(),
                    dataVO.getErrorCode());
            assertEquals(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM.getErrorMessage(),
                    dataVO.getErrorMessage());

            dataVO = invoke.get("http://localhost:9999/service/exception", DataVO.class);
            assertNotNull(dataVO);
            assertEquals(UserInterfaceExceptionMapper.NOT_CAPTURED_CODE, dataVO.getErrorCode());
            assertEquals(UserInterfaceExceptionMapper.NOT_CAPTURED_MSG, dataVO.getErrorMessage());

            invoke.close();
        } catch (RestInvokeException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}

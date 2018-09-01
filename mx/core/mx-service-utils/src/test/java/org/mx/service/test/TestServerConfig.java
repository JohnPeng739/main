package org.mx.service.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.rest.cors.CorsConfigBean;
import org.mx.service.server.CommServerConfigBean;
import org.mx.service.server.RestfulServerConfigBean;
import org.mx.service.server.ServletServerConfigBean;
import org.mx.service.server.WebsocketServerConfigBean;
import org.mx.service.test.config.TestServerConfigConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

/**
 * 描述： 测试服务器配置对象的单元测试
 *
 * @author john peng
 * Date time 2018/7/19 下午1:46
 */
public class TestServerConfig {
    private AnnotationConfigApplicationContext context;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestServerConfigConfig.class);
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void testRestfulServerConfig() {
        RestfulServerConfigBean config = context.getBean(RestfulServerConfigBean.class);
        assertNotNull(config);

        assertTrue(config.isEnabled());
        assertEquals(7000, config.getPort());
        assertTrue(config.isSecurity());
        assertEquals("restful-keystore-path", config.getKeystorePath());
        assertEquals("restful-keystore-password", config.getKeystorePassword());
        assertEquals("restful-keystore-manager-password", config.getKeyManagerPassword());
        assertEquals(123, config.getThreads());
        assertEquals(100 * 1024, config.getOutputSize());
        assertEquals(16 * 1024, config.getRequestHeaderSize());
        assertEquals(15 * 1024, config.getResponseHeaderSize());
        assertEquals(2000, config.getIdleTimeoutSecs());
        assertEquals(2, config.getServiceClasses().length);
        assertEquals("restful-classes1", config.getServiceClasses()[0]);
        assertEquals("restful-classes2", config.getServiceClasses()[1]);
    }

    @Test
    public void testServletServerConfig() {
        ServletServerConfigBean config = context.getBean(ServletServerConfigBean.class);
        assertNotNull(config);

        assertTrue(config.isEnabled());
        assertEquals(7001, config.getPort());
        assertTrue(config.isSecurity());
        assertEquals("servlet-keystore-path", config.getKeystorePath());
        assertEquals("servlet-keystore-password", config.getKeystorePassword());
        assertEquals("servlet-keystore-manager-password", config.getKeyManagerPassword());
        assertEquals(321, config.getThreads());
        assertEquals(50 * 1024, config.getOutputSize());
        assertEquals(10 * 1024, config.getRequestHeaderSize());
        assertEquals(20 * 1024, config.getResponseHeaderSize());
        assertEquals(100, config.getIdleTimeoutSecs());
        assertEquals(2, config.getServiceClasses().length);
        assertEquals("servlet-classes1", config.getServiceClasses()[0]);
        assertEquals("servlet-classes2", config.getServiceClasses()[1]);
    }

    @Test
    public void testWebSocketServerConfig() {
        WebsocketServerConfigBean config = context.getBean(WebsocketServerConfigBean.class);
        assertNotNull(config);

        assertTrue(config.isEnabled());
        assertEquals(7002, config.getPort());
        assertTrue(config.isSecurity());
        assertEquals("websocket-keystore-path", config.getKeystorePath());
        assertEquals("websocket-keystore-password", config.getKeystorePassword());
        assertEquals("websocket-keystore-manager-password", config.getKeyManagerPassword());
        assertEquals(10, config.getPingCycleSec());
        assertEquals(40, config.getCleanCycleSec());
        assertEquals(110, config.getThreads());
        assertEquals(30 * 1024, config.getOutputSize());
        assertEquals(10 * 1024, config.getRequestHeaderSize());
        assertEquals(12 * 1024, config.getResponseHeaderSize());
        assertEquals(350, config.getIdleTimeoutSecs());
        assertEquals(30, config.getAsyncWriteTimeoutSecs());
        assertEquals(6 * 1024, config.getInputBufferSize());
        assertEquals(60 * 1024, config.getMaxTextMessageSize());
        assertEquals(33 * 1024, config.getMaxTextMessageBufferSize());
        assertEquals(62 * 1024, config.getMaxBinaryMessageSize());
        assertEquals(12 * 1024, config.getMaxBinaryMessageBufferSize());
        assertEquals(2, config.getServiceClasses().length);
        assertEquals("websocket-classes1", config.getServiceClasses()[0]);
        assertEquals("websocket-classes2", config.getServiceClasses()[1]);
    }

    @Test
    public void testCommServerConfig() {
        CommServerConfigBean config = context.getBean(CommServerConfigBean.class);
        assertNotNull(config);

        assertTrue(config.isTcpEnabled());
        assertEquals(1, config.getTcpServerNum());
        assertEquals(1, config.getTcpServers().size());
        assertNotNull(config.getTcpServers().get(0));
        assertEquals(9996, config.getTcpServers().get(0).getPort());
        assertEquals(500, config.getTcpServers().get(0).getMaxLength());
        assertEquals(321, config.getTcpServers().get(0).getMaxTimeout());
        assertEquals("org.mx.service.server.comm.DefaultPacketWrapper", config.getTcpServers().get(0).getPacketWrapper());
        assertEquals("tcpReceiver", config.getTcpServers().get(0).getReceiver());

        assertTrue(config.isUdpEnabled());
        assertEquals(1, config.getUdpServerNum());
        assertEquals(1, config.getUdpServers().size());
        assertNotNull(config.getUdpServers().get(0));
        assertEquals(9995, config.getUdpServers().get(0).getPort());
        assertEquals(50, config.getUdpServers().get(0).getMaxLength());
        assertEquals(123, config.getUdpServers().get(0).getMaxTimeout());
        assertEquals("org.mx.service.server.comm.DefaultPacketWrapper", config.getUdpServers().get(0).getPacketWrapper());
        assertEquals("udpReceiver", config.getUdpServers().get(0).getReceiver());
    }

    @Test
    public void testCorsConfig() {
        CorsConfigBean config = context.getBean(CorsConfigBean.class);
        assertNotNull(config);

        assertTrue(config.isEnabled());
        assertEquals("http://www.baidu.com/", config.getAllowOrigin());
        assertEquals("origin, content-type, accept, authorization, token", config.getAllowHeaders());
        assertEquals("X-My-Custorm-Header, X-Custom-Header", config.getExposeHeader());
        assertEquals("GET, POST, PUT, DELETE, OPTIONS", config.getAllowMethods());
        assertEquals("true", config.getAllowCredentials());
        assertEquals("12096000", config.getMaxAge());
    }
}

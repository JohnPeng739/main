package org.mx.service.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.client.comm.CommClientInvoke;
import org.mx.service.server.CommServerFactory;
import org.mx.service.server.comm.CommServiceProvider;
import org.mx.service.server.comm.DefaultPacketWrapper;
import org.mx.service.server.comm.TcpCommServiceProvider;
import org.mx.service.test.comm.TestTcpReceiver;
import org.mx.service.test.config.TestTcpConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class TestTcpServer {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestTcpConfig.class);
    }

    @After
    public void after() {
        context.close();
    }

    @Test
    public void testTcpCommServer() {
        CommServerFactory factory = context.getBean(CommServerFactory.class);
        assertNotNull(factory);
        Integer port = context.getEnvironment().getProperty("tcp.servers.1.port", Integer.class);
        assertNotNull(port);
        TcpCommServiceProvider provider = factory.getTcpProvider(port);
        assertNotNull(provider);
        TestTcpReceiver tcpReceiver = context.getBean("tcpReceiver", TestTcpReceiver.class);
        assertNotNull(tcpReceiver);

        CommClientInvoke client = new CommClientInvoke(CommServiceProvider.CommServiceType.TCP, tcpReceiver,
                new DefaultPacketWrapper(), "127.0.0.1", port, 40 + 12, 1000);
        try {
            // 等待后台建立连接
            Thread.sleep(1000);
            byte[] payload = "test message".getBytes();
            client.send(payload);
            Thread.sleep(1000);
            assertNotNull(tcpReceiver.getPayload());
            assertArrayEquals(payload, tcpReceiver.getPayload());

            // 在默认的包装器下，最大数据载荷为：length - 12
            payload = "1234567890123456789012345678901234567890".getBytes();
            client.send(payload);
            Thread.sleep(1000);
            assertNotNull(tcpReceiver.getPayload());
            assertArrayEquals(payload, tcpReceiver.getPayload());

            try {
                payload = "12345678901234567890123456789012345678901".getBytes();
                client.send(payload);
                Thread.sleep(1000);
                fail("Here need a exception.");
            } catch (Exception ex) {
                // success
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}

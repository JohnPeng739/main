package org.mx.service.test.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.client.comm.CommClientInvoke;
import org.mx.service.server.CommServerFactory;
import org.mx.service.server.comm.CommServiceProvider;
import org.mx.service.server.comm.DefaultPacketWrapper;
import org.mx.service.server.comm.TcpCommServiceProvider;
import org.mx.service.test.server.comm.TestTcpReceiver;
import org.mx.service.test.server.config.TestTcpConfig;
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
        TcpCommServiceProvider provider = factory.getTcpProvider(9996);
        assertNotNull(provider);
        TestTcpReceiver tcpReceiver = context.getBean("tcpReceiver", TestTcpReceiver.class);
        assertNotNull(tcpReceiver);
        Integer port = context.getEnvironment().getProperty("tcp.servers.1.port", Integer.class);
        assertNotNull(port);
        Integer length = context.getEnvironment().getProperty("tcp.servers.1.maxLength", Integer.class);
        Integer timeout = context.getEnvironment().getProperty("tcp.servers.1.maxTimeout", Integer.class);

        CommClientInvoke client = new CommClientInvoke(CommServiceProvider.CommServiceType.TCP, tcpReceiver,
                new DefaultPacketWrapper(), "127.0.0.1", port, length, timeout);
        try {
            // 等待后台建立连接
            Thread.sleep(1000);
            byte[] payload = "test message".getBytes();
            client.send(payload);
            Thread.sleep(3000);
            assertNotNull(tcpReceiver.getPayload());
            assertArrayEquals(payload, tcpReceiver.getPayload());

            // 在默认的包装器下，最大数据载荷为：length - 12
            payload = "123456789012345678901234567890123456".getBytes();
            client.send(payload);
            Thread.sleep(5000);
            assertNotNull(tcpReceiver.getPayload());
            assertArrayEquals(payload, tcpReceiver.getPayload());

            try {
                payload = "12345678901234567890123456789012345678901234567890".getBytes();
                client.send(payload);
                Thread.sleep(1200);
                System.out.println(new String(tcpReceiver.getPayload()));
            } catch (Exception ex) {
                // success
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}

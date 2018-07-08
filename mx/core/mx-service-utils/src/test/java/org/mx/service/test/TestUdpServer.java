package org.mx.service.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.client.comm.CommClientInvoke;
import org.mx.service.server.CommServerFactory;
import org.mx.service.server.comm.CommServiceProvider;
import org.mx.service.server.comm.DefaultPacketWrapper;
import org.mx.service.server.comm.UdpCommServiceProvider;
import org.mx.service.test.comm.TestUdpReceiver;
import org.mx.service.test.config.TestUdpConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.*;

public class TestUdpServer {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestUdpConfig.class);
    }

    @After
    public void after() {
        context.close();
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
            Thread.sleep(500);
            assertNotNull(udpReceiver.getPayload());
            assertArrayEquals(payload, udpReceiver.getPayload());

            // 在默认的包装器下，最大数据载荷为：length - 12
            payload = "12345678901234567890123456789012345678901234567890".getBytes();
            client.send(payload);
            Thread.sleep(500);
            assertNotNull(udpReceiver.getPayload());
            assertArrayEquals(payload, udpReceiver.getPayload());

            try {
                payload = "123456789012345678901234567890123456789012345678901".getBytes();
                client.send(payload);
                fail("Here need any exception");
            } catch (Exception ex) {
                // success
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
        client.close();
    }
}

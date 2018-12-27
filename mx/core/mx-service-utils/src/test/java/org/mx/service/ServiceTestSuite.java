package org.mx.service;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.mx.service.test.*;

/**
 * 描述：
 *
 * @author : john date : 2018/7/7 下午10:11
 */
public class ServiceTestSuite {
    public static Test suite() {
        TestSuite suite = new TestSuite("Test any service");
        suite.addTest(new JUnit4TestAdapter(TestGraphQLConfig.class));
        suite.addTest(new JUnit4TestAdapter(TestHttpServer.class));
        suite.addTest(new JUnit4TestAdapter(TestHttpsServer.class));
        suite.addTest(new JUnit4TestAdapter(TestServletServer.class));
        suite.addTest(new JUnit4TestAdapter(TestServletSslServer.class));
        suite.addTest(new JUnit4TestAdapter(TestTcpServer.class));
        suite.addTest(new JUnit4TestAdapter(TestUdpServer.class));
        suite.addTest(new JUnit4TestAdapter(TestWebsocketServer.class));
        suite.addTest(new JUnit4TestAdapter(TestWebsocketSslServer.class));
        suite.addTest(new JUnit4TestAdapter(TestWebsocketServerListFilterAllow.class));
        suite.addTest(new JUnit4TestAdapter(TestWebsocketServerDdosFilter.class));
        suite.addTest(new JUnit4TestAdapter(TestWebsocketServerListFilterBlock.class));
        return suite;
    }
}

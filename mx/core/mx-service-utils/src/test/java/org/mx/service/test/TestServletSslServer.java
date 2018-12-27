package org.mx.service.test;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.server.AbstractServerFactory;
import org.mx.service.server.ServletServerConfigBean;
import org.mx.service.server.ServletServerFactory;
import org.mx.service.test.config.TestServletSslConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

public class TestServletSslServer {
    private AnnotationConfigApplicationContext context = null;
    private SslContextFactory sslContextFactory = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestServletSslConfig.class);
    }

    @After
    public void after() {
        if (sslContextFactory != null) {
            try {
                sslContextFactory.stop();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        context.close();
    }

    @Test
    public void testServletServer() {
        AbstractServerFactory factory = context.getBean(ServletServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        ServletServerConfigBean config = context.getBean(ServletServerConfigBean.class);
        assertNotNull(config);

        try {
            HttpClientBuilder builder = HttpClientBuilder.create();
            sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(config.getKeystorePath());
            sslContextFactory.setKeyStorePassword(config.getKeystorePassword());
            sslContextFactory.setKeyManagerPassword(config.getKeyManagerPassword());
            sslContextFactory.start();
            builder.setSSLContext(sslContextFactory.getSslContext());
            builder.setSSLHostnameVerifier((s, sslSession) -> true);
            CloseableHttpClient client = builder.build();
            HttpGet get = new HttpGet("https://localhost:9998/download?id=123");
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
}

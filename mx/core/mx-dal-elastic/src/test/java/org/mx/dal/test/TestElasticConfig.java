package org.mx.dal.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.dal.utils.ElasticConfigBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestElasticConfig {
    private AnnotationConfigApplicationContext context;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestConfigConfig.class);
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void test() {
        ElasticConfigBean config = context.getBean(ElasticConfigBean.class);
        assertNotNull(config);

        assertEquals(1, config.getShards());
        assertEquals(1, config.getReplicas());
        assertEquals(1, config.getEntityBasePackages().length);
        assertEquals("org.mx.dal.test.entity", config.getEntityBasePackages()[0]);
        assertEquals(3, config.getServerNum());
        assertEquals(3, config.getServerConfigs().size());
        assertEquals("http", config.getServerConfigs().get(0).getProtocol());
        assertEquals("localhost", config.getServerConfigs().get(0).getServer());
        assertEquals(9200, config.getServerConfigs().get(0).getPort());
        assertEquals("http", config.getServerConfigs().get(1).getProtocol());
        assertEquals("192.168.2.146", config.getServerConfigs().get(1).getServer());
        assertEquals(9201, config.getServerConfigs().get(1).getPort());
        assertEquals("http", config.getServerConfigs().get(2).getProtocol());
        assertEquals("192.168.2.150", config.getServerConfigs().get(2).getServer());
        assertEquals(9202, config.getServerConfigs().get(2).getPort());
    }
}

package org.mx.comps.rbac;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.comps.rbac.config.TestRbacConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.assertNotNull;

public class TestUser {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestRbacConfig.class);
    }

    @After
    public void after() {
        context.close();
    }

    @Test
    public void test() {
        assertNotNull(context);
    }
}

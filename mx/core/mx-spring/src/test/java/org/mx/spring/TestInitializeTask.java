package org.mx.spring;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.spring.config.SpringConfig;
import org.mx.spring.config.TestTaskConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestInitializeTask {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestTaskConfig.class);
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void test() {
        assertNotNull(context);
    }
}

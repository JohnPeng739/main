package org.mx.comps.notify;

import org.junit.After;
import org.junit.Before;
import org.mx.comps.notify.config.TestConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BaseTest {
    protected AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestConfig.class);
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }
}

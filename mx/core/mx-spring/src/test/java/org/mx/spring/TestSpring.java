package org.mx.spring;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.spring.config.SpringConfig;
import org.mx.spring.utils.SpringContextHolder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestSpring {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(SpringConfig.class);
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
        SpringContextHolder holder = SpringContextHolder.getBean(SpringContextHolder.class);
        assertNotNull(holder);
        TestBean bean = SpringContextHolder.getBean(TestBean.class);
        assertNotNull(bean);
        assertEquals("Hello, world.", bean.hello());
        bean = SpringContextHolder.getBean("testBean", TestBean.class);
        assertNotNull(bean);
        assertEquals("Hello, world.", bean.hello());
    }
}

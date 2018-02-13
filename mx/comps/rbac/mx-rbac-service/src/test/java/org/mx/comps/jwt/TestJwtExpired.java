package org.mx.comps.jwt;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.comps.jwt.config.TestJwtConfig;
import org.mx.comps.jwt.config.TestJwtExpiredConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestJwtExpired {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestJwtExpiredConfig.class);
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

    @Test
    public void test() {
        JwtService service = context.getBean(JwtService.class);
        assertNotNull(service);
        Map<String, Object> payload = new HashMap<>();
        payload.put("user", "John.Peng");
        payload.put("roles", Arrays.asList("admin", "user"));
        String token = service.sign(payload);
        assertNotNull(token);
        assertTrue(service.verify(token));

        try {
            // 判断会延迟1秒
            Thread.sleep(3000);
            assertTrue(service.verify(token));
            Thread.sleep(900);
            assertTrue(service.verify(token));
            Thread.sleep(1200);
            assertFalse(service.verify(token));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}

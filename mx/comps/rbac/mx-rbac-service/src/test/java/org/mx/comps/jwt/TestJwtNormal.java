package org.mx.comps.jwt;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.comps.jwt.config.TestJwtConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestJwtNormal {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestJwtConfig.class);
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

        JwtVerifyFuncBuilder instance = new JwtVerifyFuncBuilder();
        assertTrue(service.verify(token, instance.fieldEquals("user", "John.Peng")));

        instance = new JwtVerifyFuncBuilder();
        assertTrue(service.verify(token, instance.fieldEquals("user", "John.Peng")
                .and(instance.fieldArrayContains("roles", Arrays.asList("admin", "user")))));

        instance = new JwtVerifyFuncBuilder();
        assertFalse(service.verify(token, instance.fieldEquals("user", "John.Peng1")));
        instance = new JwtVerifyFuncBuilder();
        assertFalse(service.verify(token, instance.fieldEquals("user", "John.Peng")
                .and(instance.fieldArrayContains("roles", Arrays.asList("admin1", "user")))));
    }
}

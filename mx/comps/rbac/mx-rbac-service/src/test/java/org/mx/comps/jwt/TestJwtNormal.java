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

        assertTrue(service.verify(token, claims -> claims != null && claims.containsKey("user") &&
                "John.Peng".equals(claims.get("user").asString())));
        assertTrue(service.verify(token, claims -> {
            if (claims != null && claims.containsKey("roles")) {
                List<String> roles = claims.get("roles").asList(String.class);
                return roles.containsAll(Arrays.asList("admin", "user"));
            }
            return false;
        }));

        assertFalse(service.verify(token, claims -> claims != null && claims.containsKey("user") &&
                "John.Peng1".equals(claims.get("user").asString())));
        assertFalse(service.verify(token, claims -> {
            if (claims != null && claims.containsKey("roles")) {
                List<String> roles = claims.get("roles").asList(String.class);
                return roles.containsAll(Arrays.asList("admin1", "user1"));
            }
            return false;
        }));
    }
}

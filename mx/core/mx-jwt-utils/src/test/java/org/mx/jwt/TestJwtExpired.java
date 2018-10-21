package org.mx.jwt;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.StringUtils;
import org.mx.jwt.config.TestJwtExpiredConfig;
import org.mx.jwt.service.JwtService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestJwtExpired {
    AnnotationConfigApplicationContext context;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestJwtExpiredConfig.class);
    }

    @After
    public void after() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void test() {
        JwtService jwtService = context.getBean(JwtService.class);
        assertNotNull(jwtService);

        try {
            String accountCode = "john.peng";
            String token = jwtService.signToken(accountCode, "3sec");
            assertFalse(StringUtils.isBlank(token));
            Thread.sleep(5000);
            JwtService.JwtVerifyResult result = jwtService.verifyToken(token);
            assertNotNull(result);
            assertFalse(result.isPassed());

            Map<String, Object> claims = new HashMap<>();
            claims.put("accountCode", accountCode);
            claims.put("login", true);
            token = jwtService.signToken(claims, "3sec");
            assertFalse(StringUtils.isBlank(token));
            Thread.sleep(5000);
            result = jwtService.verifyToken(token);
            assertNotNull(result);
            assertFalse(result.isPassed());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}

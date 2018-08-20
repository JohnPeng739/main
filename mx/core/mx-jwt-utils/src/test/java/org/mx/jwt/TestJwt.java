package org.mx.jwt;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.StringUtils;
import org.mx.jwt.config.TestJwtConfig;
import org.mx.jwt.service.JwtService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestJwt {
    AnnotationConfigApplicationContext context;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestJwtConfig.class);
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
            String token = jwtService.signToken(accountCode);
            assertFalse(StringUtils.isBlank(token));
            JwtService.JwtVerifyResult result = jwtService.verifyToken(token);
            assertNotNull(result);
            assertTrue(result.isPassed());
            assertFalse(StringUtils.isBlank(result.getHeader()));
            assertFalse(StringUtils.isBlank(result.getPayload()));
            assertFalse(StringUtils.isBlank(result.getSignature()));
            assertNotNull(result.getClaims());
            assertTrue(result.getClaims().containsKey("accountCode"));
            assertTrue(result.getClaims().get("accountCode").asString().equals(accountCode));

            Map<String, Object> claims = new HashMap<>();
            claims.put("accountCode", accountCode);
            claims.put("login", true);
            token = jwtService.signToken(claims);
            assertFalse(StringUtils.isBlank(token));
            result = jwtService.verifyToken(token);
            assertNotNull(result);
            assertTrue(result.isPassed());
            assertFalse(StringUtils.isBlank(result.getHeader()));
            assertFalse(StringUtils.isBlank(result.getPayload()));
            assertFalse(StringUtils.isBlank(result.getSignature()));
            assertNotNull(result.getClaims());
            assertTrue(result.getClaims().containsKey("accountCode"));
            assertTrue(result.getClaims().get("accountCode").asString().equals(accountCode));
            assertTrue(result.getClaims().containsKey("login"));
            assertTrue(result.getClaims().get("login").asBoolean());

            result = jwtService.verifyToken(token,
                    JwtService.JwtVerifyPredicateBuilder.eq("accountCode", accountCode).and(
                            JwtService.JwtVerifyPredicateBuilder.eq("login", true)
                    ));
            assertNotNull(result);
            assertTrue(result.isPassed());
            assertFalse(StringUtils.isBlank(result.getHeader()));
            assertFalse(StringUtils.isBlank(result.getPayload()));
            assertFalse(StringUtils.isBlank(result.getSignature()));
            assertNotNull(result.getClaims());
            assertTrue(result.getClaims().containsKey("accountCode"));
            assertTrue(result.getClaims().get("accountCode").asString().equals(accountCode));
            assertTrue(result.getClaims().containsKey("login"));
            assertTrue(result.getClaims().get("login").asBoolean());

            result = jwtService.verifyToken(token,
                    JwtService.JwtVerifyPredicateBuilder.eq("accountCode", accountCode).and(
                            JwtService.JwtVerifyPredicateBuilder.eq("login", false)
                    ));
            assertNotNull(result);
            assertFalse(result.isPassed());

            result = jwtService.verifyToken(token,
                    JwtService.JwtVerifyPredicateBuilder.eq("accountCode", "NA").and(
                            JwtService.JwtVerifyPredicateBuilder.eq("login", true)
                    ));
            assertNotNull(result);
            assertFalse(result.isPassed());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }
}

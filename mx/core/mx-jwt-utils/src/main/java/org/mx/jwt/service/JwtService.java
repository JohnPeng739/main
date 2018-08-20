package org.mx.jwt.service;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 描述： JWT(JSON Web Tokens)服务接口定义
 *
 * @author john peng
 * Date time 2018/8/19 下午7:20
 */
public interface JwtService {
    String signToken(String accountCode);

    String signToken(Map<String, Object> claims);

    JwtVerifyResult verifyToken(String token);

    JwtVerifyResult verifyToken(String token, Predicate<Map<String, Claim>> predicate);

    class JwtVerifyResult {
        private boolean passed = false;
        private String header, payload,signature;
        private Map<String, Claim> claims;

        public JwtVerifyResult() {
            super();
        }

        public JwtVerifyResult(DecodedJWT decodedJWT) {
            this();
            passed = true;
            header = decodedJWT.getHeader();
            payload = decodedJWT.getPayload();
            signature = decodedJWT.getSignature();
            claims = decodedJWT.getClaims();
        }

        public boolean isPassed() {
            return passed;
        }

        public String getHeader() {
            return header;
        }

        public String getPayload() {
            return payload;
        }

        public String getSignature() {
            return signature;
        }

        public Map<String, Claim> getClaims() {
            return claims;
        }
    }

    class JwtVerifyPredicateBuilder {
        public static Predicate<Map<String, Claim>> eq(String key, Object value) {
            return claims -> claims != null && claims.containsKey(key) && value != null &&
                    claimEquals(value, claims.get(key));
        }

        public static Predicate<Map<String, Claim>> arrayContains(String key, List<Object> array) {
            return claims -> claims != null && claims.containsKey(key) && array != null &&
                    claimEquals(array, claims.get(key));
        }

        private static boolean claimEquals(Object v, Claim claim) {
            if (v instanceof Boolean) {
                return claim.asBoolean().equals(v);
            } else if (v instanceof Integer) {
                return claim.asInt().equals(v);
            } else if (v instanceof Long) {
                return claim.asLong().equals(v);
            } else if (v instanceof Date) {
                return claim.asDate().equals(v);
            } else if (v instanceof String) {
                return claim.asString().equals(v);
            } else if (v instanceof Double) {
                return claim.asDouble().equals(v);
            } else if (v instanceof List) {
                return claim.asList(String.class).containsAll((Collection<?>) v);
            } else {
                // unsupported type, transform to string
                return claim.asString().equals(v);
            }
        }
    }
}

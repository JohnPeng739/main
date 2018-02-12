package org.mx.comps.jwt;

import com.auth0.jwt.interfaces.Claim;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * JWT自定义验证函数构建工具
 *
 * @author : john.peng created on date : 2018/2/12
 */
public class JwtVerifyFuncBuilder {
    private boolean claimEquals(Object v, Claim claim) {
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

    public Predicate<Map<String, Claim>> fieldEquals(String key, Object value) {
        return new Predicate<Map<String, Claim>>() {
            @Override
            public boolean test(Map<String, Claim> claims) {
                return claims != null && claims.containsKey(key) && value != null && claimEquals(value, claims.get(key));
            }
        };
    }

    public Predicate<Map<String, Claim>> fieldArrayContains(String key, List<Object> value) {
        return new Predicate<Map<String, Claim>>() {
            @Override
            public boolean test(Map<String, Claim> claims) {
                return claims != null && claims.containsKey(key) && value != null && claimEquals(value, claims.get(key));
            }
        };
    }
}

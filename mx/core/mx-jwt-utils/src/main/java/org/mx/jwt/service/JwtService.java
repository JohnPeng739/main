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
    /**
     * 签发一个JWT身份令牌
     *
     * @param accountCode 账号
     * @return JWT身份令牌
     */
    String signToken(String accountCode);

    /**
     * 签发一个JWT身份令牌
     *
     * @param claims 令牌中包含的载荷信息
     * @return JWT身份令牌
     */
    String signToken(Map<String, Object> claims);

    /**
     * 验证一个JWT身份令牌
     *
     * @param token 身份令牌
     * @return 验证结果
     * @see #verifyToken(String, Predicate)
     */
    JwtVerifyResult verifyToken(String token);

    /**
     * 验证一个JWT身份令牌
     *
     * @param token     身份令牌
     * @param predicate 额外的验证规则，一般都是对载荷数据进行验证。
     * @return 验证结果
     * @see JwtVerifyResult
     */
    JwtVerifyResult verifyToken(String token, Predicate<Map<String, Claim>> predicate);

    /**
     * JWT身份令牌验证结果类定义
     */
    class JwtVerifyResult {
        private boolean passed = false;
        private String header, payload, signature;
        private Map<String, Claim> claims;

        /**
         * 默认构造函数
         */
        public JwtVerifyResult() {
            super();
        }

        /**
         * 构造函数
         *
         * @param decodedJWT 已解码的JWT对象
         */
        public JwtVerifyResult(DecodedJWT decodedJWT) {
            this();
            passed = true;
            header = decodedJWT.getHeader();
            payload = decodedJWT.getPayload();
            signature = decodedJWT.getSignature();
            claims = decodedJWT.getClaims();
        }

        /**
         * 获取JWT身份是否验证通过
         *
         * @return 返回true表示验证通过，否则表示验证不通过
         */
        public boolean isPassed() {
            return passed;
        }

        /**
         * 获取采用BASE64编码的JWT头
         *
         * @return JWT头
         */
        public String getHeader() {
            return header;
        }

        /**
         * 获取采用BASE64编码的JWT载荷数据
         *
         * @return JWT载荷
         */
        public String getPayload() {
            return payload;
        }

        /**
         * 获取采用BASE64编码的JWT签名
         *
         * @return JWT签名
         */
        public String getSignature() {
            return signature;
        }

        /**
         * 获取JWT身份令牌中的有效载荷数据
         *
         * @return 有效载荷数据
         */
        public Map<String, Claim> getClaims() {
            return claims;
        }
    }

    /**
     * JWT身份令牌验证规则构建器
     */
    class JwtVerifyPredicateBuilder {
        /**
         * 构造一个指定载荷数据是否和指定的值相符
         *
         * @param key   载荷数据的Key
         * @param value 指定的值
         * @return 如果相符则返回true，否则返回false。
         */
        public static Predicate<Map<String, Claim>> eq(String key, Object value) {
            return claims -> claims != null && claims.containsKey(key) && value != null &&
                    claimEquals(value, claims.get(key));
        }

        /**
         * 构造一个指定载荷数据是否在指定列表数据之内
         *
         * @param key   载荷数据的Key
         * @param array 指定的列表数据
         * @return 如果指定载荷数据在列表数据范围内，则返回true；否则返回false。
         */
        public static Predicate<Map<String, Claim>> arrayContains(String key, List<Object> array) {
            return claims -> claims != null && claims.containsKey(key) && array != null &&
                    claimEquals(array, claims.get(key));
        }

        /**
         * 验证指定的数据对象是否和指定载荷相符
         *
         * @param v     数据对象
         * @param claim 指定的载荷
         * @return 如果相符则返回true，否则返回false。
         */
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

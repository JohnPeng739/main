package org.mx.comps.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.TypeUtils;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.Predicate;

/**
 * Jwt服务类
 *
 * @author : john.peng created on date : 2018/1/17
 */
@Component
public class JwtService {
    private static final Log logger = LogFactory.getLog(JwtService.class);

    private Environment env;

    private boolean hasInitialized = false;
    private String issue, subject;
    private int expiredClock;
    private long timePeriod;
    private Algorithm algorithm;
    private JWTVerifier verifier;

    @Autowired
    public JwtService(Environment env) {
        super();
        this.env = env;
    }

    /**
     * 初始化
     */
    private void init() {
        if (hasInitialized) {
            return;
        }
        String algorithmName = env.getProperty("auth.algorithm", "HS256");
        issue = env.getProperty("auth.issue", "mx institute");
        subject = env.getProperty("auth.subject", "everyone");
        String expired = env.getProperty("auth.expired", "2 Sec");
        expiredClock = env.getProperty("auth.expiredClock", Integer.class, -1);
        timePeriod = TypeUtils.string2TimePeriod(expired, TypeUtils.DAY);
        try {
            String secret = "john_73_9@hotmail.com";
            switch (algorithmName) {
                case "HS256":
                    algorithm = Algorithm.HMAC256(secret);
                    break;
                case "HS384":
                    algorithm = Algorithm.HMAC384(secret);
                    break;
                case "HS512":
                    algorithm = Algorithm.HMAC512(secret);
                    break;
                default:
                    throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.UNSUPPORTED_ALGORITHM);
            }
            verifier = JWT.require(algorithm)
                    .withIssuer(issue)
                    .withSubject(subject)
                    .acceptLeeway(1)
                    .acceptExpiresAt(1)
                    .build();
            hasInitialized = true;
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Initialize a JWT instance, algorithm: %s, issue: %s, subject: %s.",
                        algorithmName, issue, subject));
            }
        } catch (UnsupportedEncodingException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Initialize JWT fail.", ex);
            }
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.UNSUPPORTED_ALGORITHM);
        }
    }

    /**
     * 直接的令牌验证
     *
     * @param token 令牌数据
     * @return 验证结果
     * @see JwtVerifyResult
     */
    public JwtVerifyResult verify(String token) {
        return verify(token, null);
    }

    /**
     * 直接的令牌验证
     *
     * @param token    令牌数据
     * @param fnVerify 自定义的校验方法，接收数据结集合，校验成功返回true，否则返回false。
     * @return 验证结果
     * @see JwtVerifyResult
     */
    public JwtVerifyResult verify(String token, Predicate<Map<String, Claim>> fnVerify) {
        if (StringUtils.isBlank(token)) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.NOT_AUTHENTICATED);
        }
        try {
            DecodedJWT jwt = verifier.verify(token);
            if (fnVerify != null) {
                // 有自定义校验方法
                return fnVerify.test(jwt.getClaims()) ? new JwtVerifyResult(jwt) : new JwtVerifyResult();
            } else {
                // 签名校验成功，无自定义校验方法
                return new JwtVerifyResult(jwt);
            }
        } catch (JWTVerificationException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Verify the JWT fail.", ex);
            }
            return new JwtVerifyResult();
        }
    }

    /**
     * 根据传输的验证内容，对登录后的用户生成身份令牌
     *
     * @param claims 需要验证的载荷内容
     * @return 身份令牌
     */
    @SuppressWarnings("unchecked")
    public String sign(Map<String, Object> claims) {
        init();
        Date expiredDate = new Date(System.currentTimeMillis() + timePeriod);
        if (timePeriod > TypeUtils.DAY && expiredClock >= 0 && expiredClock <= 24) {
            // 如果配置了一般过期时间检查点，强制设置过期时间点的小时数。
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expiredDate);
            calendar.set(Calendar.HOUR_OF_DAY, expiredClock);
            expiredDate = calendar.getTime();
        }
        JWTCreator.Builder builder = JWT.create().withIssuer(issue).withSubject(subject).withExpiresAt(expiredDate);
        if (claims != null && !claims.isEmpty()) {
            claims.forEach((k, v) -> {
                if (v instanceof Boolean) {
                    builder.withClaim(k, (Boolean) v);
                } else if (v instanceof Integer) {
                    builder.withClaim(k, (Integer) v);
                } else if (v instanceof Long) {
                    builder.withClaim(k, (Long) v);
                } else if (v instanceof Date) {
                    builder.withClaim(k, (Date) v);
                } else if (v instanceof String) {
                    builder.withClaim(k, (String) v);
                } else if (v instanceof Double) {
                    builder.withClaim(k, (Double) v);
                } else if (v instanceof List) {
                    String[] value = ((List<String>) v).toArray(new String[0]);
                    builder.withArrayClaim(k, value);
                } else {
                    // unsupported type, transform to string
                    builder.withClaim(k, v.toString());
                }
            });
        }
        String token = builder.sign(algorithm);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Create a token: %s.", token));
        }
        return token;
    }

    /**
     * 对登录后的用户生成身份令牌
     *
     * @param userCode 用户代码
     * @return 身份令牌
     */
    public String sign(String userCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("user", userCode);
        return sign(map);
    }

    /**
     * JWT验证结果定义
     */
    public class JwtVerifyResult {
        private boolean passed = false;
        private String header, payload, signature;
        private Map<String, Claim> claims;

        /**
         * 默认的构造函数，构造一个验证不通过的结果
         */
        private JwtVerifyResult() {
            super();
        }

        /**
         * 构造函数，构造一个通过的结果
         *
         * @param jwt JWT解码对象
         */
        private JwtVerifyResult(DecodedJWT jwt) {
            this();
            passed = true;
            header = jwt.getHeader();
            payload = jwt.getPayload();
            signature = jwt.getSignature();
            claims = jwt.getClaims();
        }

        /**
         * 获取验证是否通过
         *
         * @return 返回true表示令牌验证通过，否则不通过
         */
        public boolean isPassed() {
            return passed;
        }

        /**
         * 获取令牌中的头段，采用BASE64编码
         *
         * @return 头
         */
        public String getHeader() {
            return header;
        }

        /**
         * 获取令牌中的载荷段，采用BASE64编码
         *
         * @return 载荷
         */
        public String getPayload() {
            return payload;
        }

        /**
         * 获取令牌中的签名段，采用BASE64编码
         *
         * @return 签名
         */
        public String getSignature() {
            return signature;
        }

        /**
         * 获取令牌的中载荷数据，Map集合方式
         *
         * @return 载荷数据
         */
        public Map<String, Claim> getClaims() {
            return claims;
        }
    }
}

package org.mx.comps.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.glassfish.jersey.server.ContainerRequest;
import org.mx.StringUtils;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Jwt服务类
 *
 * @author : john.peng created on date : 2018/1/17
 */
@Component
public class JwtService {
    @Autowired
    private Environment env = null;

    private boolean hasInitialized = false;
    private String issue, subject;
    private int expiredClock;
    private long timePeriod;
    private Algorithm algorithm;
    private JWTVerifier verifier;

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
        String expired = env.getProperty("auth.expired", "1 DAY");
        expiredClock = env.getProperty("auth.expiredClock", Integer.class, -1);
        timePeriod = StringUtils.stirng2TimePeriod(expired, StringUtils.DAY);
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
                    .acceptExpiresAt(5)
                    .build();
            hasInitialized = true;
        } catch (UnsupportedEncodingException ex) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.UNSUPPORTED_ALGORITHM);
        }
    }

    /**
     * Http请求的令牌身份认证，令牌一般在HTTP的token或Authorization头中。
     *
     * @param request HTTP请求
     * @return 如果验证通过，返回true；否则返回false
     */
    public boolean verify(ContainerRequest request) {
        return verify(request, null);
    }

    /**
     * Http请求的令牌身份认证，令牌一般在HTTP的token或Authorization头中。
     *
     * @param request  HTTP请求
     * @param fnVerify 自定义的校验方法，接收数据结集合，校验成功返回true，否则返回false。
     * @return 如果验证通过，返回true；否则返回false
     */
    public boolean verify(ContainerRequest request, Predicate<Map<String, Claim>> fnVerify) {
        init();
        String token = request.getHeaderString("token");
        if (StringUtils.isBlank(token)) {
            token = request.getHeaderString("Authorization");
            if (!StringUtils.isBlank(token) && token.startsWith("Bearer ")) {
                token = token.substring("Bearer ".length());
            }
        }
        if (StringUtils.isBlank(token)) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.NOT_AUTHENTICATED);
        }
        DecodedJWT jwt = verifier.verify(token);
        if (jwt == null) {
            // 签名校验失败
            return false;
        } else if (fnVerify != null) {
            // 有自定义校验方法
            return fnVerify.test(jwt.getClaims());
        } else {
            // 签名校验成功，无自定义校验方法
            return true;
        }
    }

    /**
     * 根据传输的验证内容，对登录后的用户生成身份令牌
     *
     * @param claims 需要验证的载荷内容
     * @return 身份令牌
     */
    public String sign(Map<String, Object> claims) {
        init();
        Date expiredDate = new Date(System.currentTimeMillis() + timePeriod);
        if (expiredClock >= 0 && expiredClock <= 24) {
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
                } else {
                    // unsupported type, transform to string
                    builder.withClaim(k, v.toString());
                }
            });
        }
        return builder.sign(algorithm);
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
}

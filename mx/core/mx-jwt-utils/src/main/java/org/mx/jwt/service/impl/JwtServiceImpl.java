package org.mx.jwt.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.jwt.config.AuthConfigBean;
import org.mx.jwt.error.UserInterfaceJwtErrorException;
import org.mx.jwt.service.JwtService;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 描述： JWT（JSON Web Tokens）服务实现类
 *
 * @author john peng
 * Date time 2018/8/19 下午7:21
 */
public class JwtServiceImpl implements JwtService {
    private static final Log logger = LogFactory.getLog(JwtServiceImpl.class);

    private AuthConfigBean authConfigBean;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    /**
     * 构造函数
     *
     * @param authConfigBean 服务配置信息对象
     */
    public JwtServiceImpl(AuthConfigBean authConfigBean) {
        super();
        this.authConfigBean = authConfigBean;
    }

    /**
     * 根据指定的算法名称构造对应的算法对象
     *
     * @param algorithm 算法名称
     * @return 算法对象
     */
    private Algorithm getAlgorithm(String algorithm) {
        if (algorithm.startsWith("HS")) {
            return getHsAlgorithm(algorithm);
        } else if (algorithm.startsWith("RSA")) {
            return getRsaAlgorithm(algorithm);
        } else {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Unsupported algorithm: %s.", algorithm));
            }
            throw new UserInterfaceJwtErrorException(
                    UserInterfaceJwtErrorException.JwtErrors.JWT_INITIALIZE_FAIL
            );
        }
    }

    /**
     * 根据指定的HS（HMAC）算法名称，构造对应的RSA算法对象
     *
     * @param algorithm 算法名称
     * @return HS算法对象
     */
    private Algorithm getHsAlgorithm(String algorithm) {
        AuthConfigBean.HsConfigBean hsConfigBean = authConfigBean.getHsConfig();
        String secret = hsConfigBean.getSecret();
        try {
            switch (algorithm) {
                case "HS512":
                    return Algorithm.HMAC512(secret);
                case "HS384":
                    return Algorithm.HMAC384(secret);
                case "HS256":
                default:
                    return Algorithm.HMAC256(secret);
            }
        } catch (UnsupportedEncodingException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Initialize JWT fail.", ex);
            }
            throw new UserInterfaceJwtErrorException(
                    UserInterfaceJwtErrorException.JwtErrors.JWT_INITIALIZE_FAIL
            );
        }
    }

    /**
     * 根据指定的RSA算法名称，构造对应的RSA算法对象
     *
     * @param algorithm 算法名称
     * @return RSA算法对象
     */
    private Algorithm getRsaAlgorithm(String algorithm) {
        AuthConfigBean.RsaConfigBean rsaConfigBean = authConfigBean.getRsaConfig();
        String keystore = rsaConfigBean.getKeystore(),
                password1 = rsaConfigBean.getPassword1(),
                password2 = rsaConfigBean.getPassword2(),
                alias = rsaConfigBean.getAlias();
        if (StringUtils.isBlank(keystore) || StringUtils.isBlank(password1) || StringUtils.isBlank(password2)
                || StringUtils.isBlank(algorithm)) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Invalid parameter, keystore: %s, password1: %s, password2: %s, alias: %s.",
                        keystore, password1, password2, alias));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        try (FileInputStream fis = new FileInputStream(rsaConfigBean.getKeystore())) {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(fis, rsaConfigBean.getPassword1().toCharArray());
            RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) keyStore.getKey(alias, password2.toCharArray());
            RSAPublicKeySpec spec = new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent());
            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
            switch (algorithm) {
                case "RSA512":
                    return Algorithm.RSA512(publicKey, privateKey);
                case "RSA384":
                    return Algorithm.RSA384(publicKey, privateKey);
                case "RSA256":
                default:
                    return Algorithm.RSA256(publicKey, privateKey);
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Initialize the JWT fail.", ex);
            }
            throw new UserInterfaceJwtErrorException(
                    UserInterfaceJwtErrorException.JwtErrors.JWT_INITIALIZE_FAIL
            );
        }
    }

    /**
     * 初始化相关的资源
     */
    public void init() {
        try {
            algorithm = getAlgorithm(authConfigBean.getAlgorithm());

            verifier = JWT.require(algorithm)
                    .withIssuer(authConfigBean.getIssuer())
                    .withSubject(authConfigBean.getSubject())
                    .acceptLeeway(1)
                    .acceptExpiresAt(1)
                    .build();
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Initialize the JWT fail.", ex);
            }
            throw new UserInterfaceJwtErrorException(
                    UserInterfaceJwtErrorException.JwtErrors.JWT_INITIALIZE_FAIL
            );
        }
    }

    /**
     * 销毁相关的资源
     */
    public void destroy() {
        if (verifier != null) {
            verifier = null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see JwtService#signToken(String)
     */
    @Override
    public String signToken(String accountCode) {
        Map<String, Object> claims = new HashMap<>(1);
        claims.put("accountCode", accountCode);
        return signToken(claims);
    }

    /**
     * {@inheritDoc}
     *
     * @see JwtService#signToken(Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public String signToken(Map<String, Object> claims) {
        Date expiredDate = new Date(System.currentTimeMillis() + authConfigBean.getExpired());
        JWTCreator.Builder builder = JWT.create()
                .withIssuer(authConfigBean.getIssuer())
                .withSubject(authConfigBean.getSubject())
                .withExpiresAt(expiredDate);

        if (builder == null || algorithm == null) {
            throw new UserInterfaceJwtErrorException(
                    UserInterfaceJwtErrorException.JwtErrors.JWT_NOT_INITIALIZE
            );
        }
        try {
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
                logger.debug(String.format("Sign the token[%s] successfully.", token));
            }
            return token;
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Sign the token fail.", ex);
            }
            throw new UserInterfaceJwtErrorException(
                    UserInterfaceJwtErrorException.JwtErrors.JWT_SIGN_FAIL
            );
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see JwtService#verifyToken(String)
     */
    @Override
    public JwtVerifyResult verifyToken(String token) {
        return verifyToken(token, null);
    }

    /**
     * {@inheritDoc}
     *
     * @see JwtService#verifyToken(String, Predicate)
     */
    @Override
    public JwtVerifyResult verifyToken(String token, Predicate<Map<String, Claim>> predicate) {
        if (StringUtils.isBlank(token)) {
            throw new UserInterfaceJwtErrorException(
                    UserInterfaceJwtErrorException.JwtErrors.BLANK_TOKEN
            );
        }
        if (verifier == null) {
            throw new UserInterfaceJwtErrorException(
                    UserInterfaceJwtErrorException.JwtErrors.JWT_NOT_INITIALIZE
            );
        }
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            if (predicate == null) {
                return new JwtVerifyResult(decodedJWT);
            } else {
                return predicate.test(decodedJWT.getClaims()) ? new JwtVerifyResult(decodedJWT) : new JwtVerifyResult();
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Verify the token[%s] fail.", token), ex);
            }
            return new JwtVerifyResult();
        }
    }
}

package org.mx.jwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

/**
 * 描述： JWT认证配置信息类定义
 *
 * @author john peng
 * Date time 2018/8/20 下午6:35
 */
public class AuthConfigBean {
    @Value("${auth.algorithm:}")
    private String algorithm;
    @Value("${auth.issuer:mx institute}")
    private String issuer;
    @Value("${auth.subject:Account authenticate}")
    private String subject;

    private Environment env;

    /**
     * 构造函数
     *
     * @param env Spring IoC上下午环境
     */
    public AuthConfigBean(Environment env) {
        super();
        this.env = env;
    }

    /**
     * 获取令牌使用的算法名称
     *
     * @return 算法名称
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * 获取令牌的签发者
     *
     * @return 签发者
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * 获取令牌的主题
     *
     * @return 主题
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 获取HS配置信息对象
     *
     * @return 配置信息对象
     */
    public HsConfigBean getHsConfig() {
        return new HsConfigBean(env.getProperty("auth.hs.secret"));
    }

    /**
     * 获取RSA配置信息对象
     *
     * @return 配置信息对象
     */
    public RsaConfigBean getRsaConfig() {
        return new RsaConfigBean(env.getProperty("auth.rsa.keystore"),
                env.getProperty("auth.rsa.password1"),
                env.getProperty("auth.rsa.password2"),
                env.getProperty("auth.rsa.alias"));
    }

    /**
     * HS（HMAC）配置信息类定义
     */
    public class HsConfigBean {
        private String secret;

        /**
         * 构造函数
         *
         * @param secret 秘密信息
         */
        public HsConfigBean(String secret) {
            super();
            this.secret = secret;
        }

        /**
         * 获取秘密信息
         *
         * @return 秘密信息
         */
        public String getSecret() {
            return secret;
        }
    }

    /**
     * RSA配置信息类定义
     */
    public class RsaConfigBean {
        private String keystore, password1, password2, alias;

        /**
         * 构造函数
         *
         * @param keystore  Keystore路径
         * @param password1 Keystore访问密码
         * @param password2 密钥对访问密码
         * @param alias     密钥对别名
         */
        public RsaConfigBean(String keystore, String password1, String password2, String alias) {
            super();
            this.keystore = keystore;
            this.password1 = password1;
            this.password2 = password2;
            this.alias = alias;
        }

        /**
         * 获取Keystore的文件路径
         *
         * @return 路径
         */
        public String getKeystore() {
            return keystore;
        }

        /**
         * 获取Keystore访问的密码
         *
         * @return 密码
         */
        public String getPassword1() {
            return password1;
        }

        /**
         * 获取密钥对访问的密码
         *
         * @return 密码
         */
        public String getPassword2() {
            return password2;
        }

        /**
         * 获取密钥对的别名
         *
         * @return 别名
         */
        public String getAlias() {
            return alias;
        }
    }
}

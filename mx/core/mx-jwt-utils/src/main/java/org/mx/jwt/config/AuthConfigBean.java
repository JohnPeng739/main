package org.mx.jwt.config;

import org.mx.TypeUtils;
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
    @Value("${auth.issue:mx institute}")
    private String issue;
    @Value("${auth.subject:everyone}")
    private String subject;
    @Value("${auth.expired:1 day}")
    private String expired;

    private Environment env;

    public AuthConfigBean(Environment env) {
        super();
        this.env = env;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getIssue() {
        return issue;
    }

    public String getSubject() {
        return subject;
    }

    public long getExpired() {
        return TypeUtils.string2TimePeriod(expired, TypeUtils.DAY);
    }

    public HsConfigBean getHsConfig() {
        return new HsConfigBean(env.getProperty("auth.hs.secret"));
    }

    public RsaConfigBean getRsaConfig() {
        return new RsaConfigBean(env.getProperty("auth.rsa.keystore"),
                env.getProperty("auth.rsa.password1"),
                env.getProperty("auth.rsa.password2"),
                env.getProperty("auth.rsa.alias"));
    }

    public class HsConfigBean {
        private String secret;

        public HsConfigBean(String secret) {
            super();
            this.secret = secret;
        }

        public String getSecret() {
            return secret;
        }
    }

    public class RsaConfigBean {
        private String keystore, password1, password2, alias;

        public RsaConfigBean(String keystore, String password1, String password2, String alias) {
            super();
            this.keystore = keystore;
            this.password1 = password1;
            this.password2 = password2;
            this.alias = alias;
        }

        public String getKeystore() {
            return keystore;
        }

        public String getPassword1() {
            return password1;
        }

        public String getPassword2() {
            return password2;
        }

        public String getAlias() {
            return alias;
        }
    }
}

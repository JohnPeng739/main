package org.mx.spring.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;

/**
 * 描述： Redis Standalone配置类
 *
 * @author John.Peng
 *         Date time 2018/4/25 上午11:48
 */
public class RedisStandaloneConfig {
    private static final Log logger = LogFactory.getLog(RedisStandaloneConfig.class);
    private static final String prefix = "redis.standalone";

    private String host = "localhost", password = "";
    private int port = 6379, database = 0;

    /**
     * 默认的构造韩素
     */
    private RedisStandaloneConfig() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param env Spring上下文环境
     */
    public RedisStandaloneConfig(Environment env) {
        this();
        if (env == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("The environment is null, use the default configuration.");
            }
            return;
        }
        host = env.getProperty(String.format("%s.host", prefix), "localhost");
        password = env.getProperty(String.format("%s.password", prefix), "");
        port = env.getProperty(String.format("%s.port", prefix), Integer.class, 6379);
        database = env.getProperty(String.format("%s.database", prefix), Integer.class, 0);
    }

    /**
     * 获取RedisStandaloneConfiguration对象
     *
     * @return RedisStandaloneConfiguration对象
     */
    public RedisStandaloneConfiguration get() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setDatabase(database);
        config.setPassword(RedisPassword.of(password));
        return config;
    }
}

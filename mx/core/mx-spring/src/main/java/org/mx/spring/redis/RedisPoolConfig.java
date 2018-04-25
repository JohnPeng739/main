package org.mx.spring.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 描述： Redis中的Pool配置信息
 *
 * @author John.Peng
 *         Date time 2018/4/25 上午10:53
 */
public class RedisPoolConfig {
    private static final Log logger = LogFactory.getLog(RedisPoolConfig.class);
    private static final String prefix = "redis.pool";

    private int maxIdle = 300, maxWaitMillis = 3000;
    private boolean testOnBorrow = true;

    /**
     * 默认的构造函数
     */
    private RedisPoolConfig() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param env 传入的Spring上下文环境
     */
    public RedisPoolConfig(Environment env) {
        this();
        if (env == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("The environment is null, use the default configuration.");
            }
            return;
        }
        maxIdle = env.getProperty(String.format("%s.maxIdle", prefix), Integer.class, 300);
        maxWaitMillis = env.getProperty(String.format("%s.maxWaitMillis", prefix), Integer.class, 3000);
        testOnBorrow = env.getProperty(String.format("%s.testOnBorrow", prefix), Boolean.class, true);
    }

    /**
     * 获取对应的JedisPoolConfig对象
     *
     * @return JedisPoolConfig对象
     */
    public JedisPoolConfig get() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(testOnBorrow);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        return config;
    }
}

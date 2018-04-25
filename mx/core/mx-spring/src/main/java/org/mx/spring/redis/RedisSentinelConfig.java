package org.mx.spring.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;

import java.util.Set;

/**
 * 描述： RedisSentinel配置类
 *
 * @author John.Peng
 *         Date time 2018/4/25 上午11:37
 */
public class RedisSentinelConfig {
    private static final Log logger = LogFactory.getLog(RedisSentinelConfig.class);
    private static final String prefix = "redis.sentinel";

    Set<RedisNode> nodes = null;
    private String master = "master", password = "";
    private int database = 0;

    /**
     * 默认的构造函数
     */
    private RedisSentinelConfig() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param env Spring上下文环境
     */
    public RedisSentinelConfig(Environment env) {
        this();
        if (env == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("The environment is null, use the default configuration.");
            }
            return;
        }
        master = env.getProperty(String.format("%s.master", prefix), "master");
        password = env.getProperty(String.format("%s.password", prefix), "");
        database = env.getProperty(String.format("%s.database", prefix), Integer.class, 0);
        for (int index = 0; index < 50; index++) {
            String key = String.format("%s.sentinelHostAndPorts[%d]", prefix, index);
            String node = env.getProperty(key);
            if (StringUtils.isBlank(node)) {
                // 配置结束，跳出
                break;
            }
            String[] segs = StringUtils.split(node, ":", true, true);
            if (segs == null || segs.length != 2) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The host and ports configuration[%s] invalid, it will be ignored.", node));
                }
                continue;
            }
            nodes.add(new RedisNode(segs[0], Integer.valueOf(segs[1])));
        }
    }

    /**
     * 获取RedisSentinelConfiguration对象
     *
     * @return RedisSentinelConfiguration对象
     */
    public RedisSentinelConfiguration get() {
        RedisSentinelConfiguration config = new RedisSentinelConfiguration();
        config.setDatabase(database);
        config.setMaster(master);
        config.setPassword(RedisPassword.of(password));
        config.setSentinels(nodes);
        return config;
    }
}

package org.mx.spring.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述： Redis集群配置类
 *
 * @author John.Peng
 *         Date time 2018/4/25 上午11:04
 */
public class RedisClusterConfig {
    private static final Log logger = LogFactory.getLog(RedisClusterConfig.class);
    private static final String prefix = "redis.cluster";

    private int maxRedirects = 1;
    private String password = "";
    private Set<RedisNode> nodes = null;

    /**
     * 默认的构造函数
     */
    private RedisClusterConfig() {
        super();
        nodes = new HashSet<>();
    }

    /**
     * 默认的构造函数
     *
     * @param env Spring上下文环境
     */
    public RedisClusterConfig(Environment env) {
        this();
        if (env == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("The environment is null, use the default configuration.");
            }
            return;
        }
        maxRedirects = env.getProperty(String.format("%s.maxRedirects", prefix), Integer.class, 1);
        password = env.getProperty(String.format("%s.password", prefix), "");
        for (int index = 0; index < 50; index++) {
            String key = String.format("%s.clusterHostAndPorts[%d]", prefix, index);
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
     * 获取RedisClusterConfiguration对象
     *
     * @return RedisClusterConfiguration对象
     */
    public RedisClusterConfiguration get() {
        RedisClusterConfiguration config = new RedisClusterConfiguration();
        config.setMaxRedirects(maxRedirects);
        config.setPassword(RedisPassword.of(password));
        config.setClusterNodes(nodes);
        return config;
    }
}

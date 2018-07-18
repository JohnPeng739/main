package org.mx.spring.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.*;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述： Redis配置对象
 *
 * @author john peng
 * Date time 2018/7/17 下午4:26
 */
public class RedisConfigBean {
    private static final Log logger = LogFactory.getLog(RedisConfigBean.class);

    private Environment env;

    @Value("${redis.enable:true}")
    private boolean enable;
    @Value("${redis.type:standalone}")
    private String type;
    @Value("${redis.pool.maxIdle:300}")
    private int poolMaxIdle;
    @Value("${redis.pool.maxWaitMillis:3000}")
    private int poolMaxWaitMillis;
    @Value("${redis.pool.testOnBorrow}")
    private boolean poolTestOnBorrow;

    @Value("${redis.standalone.host:localhost}")
    private String standaloneHost;
    @Value("${redis.standalone.port:6379}")
    private int standalonePort;
    @Value("${redis.standalone.database:0}")
    private int standaloneDatabase;
    @Value("${redis.standalone.password:}")
    private String standalonePassword;

    @Value("${redis.sentinel.master:master}")
    private String sentinelMaster;
    @Value("${redis.sentinel.database:0}")
    private int sentinelDatabase;
    @Value("${redis.sentinel.password:}")
    private String sentinelPassword;

    @Value("${redis.cluster.maxRedirects:1}")
    private int clusterRedirects;
    @Value("${redis.cluster.password:}")
    private String clusterPassword;

    public RedisConfigBean(Environment env) {
        super();
        this.env = env;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getType() {
        return type;
    }

    public JedisPoolConfig getPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setTestOnBorrow(poolTestOnBorrow);
        config.setMaxIdle(poolMaxIdle);
        config.setMaxWaitMillis(poolMaxWaitMillis);
        return config;
    }

    public RedisStandaloneConfiguration getStandaloneConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(standaloneHost, standalonePort);
        config.setDatabase(standaloneDatabase);
        config.setPassword(RedisPassword.of(standalonePassword));
        return config;
    }

    private Set<RedisNode> getNodes(String prefix) {
        Set<RedisNode> nodes = new HashSet<>();
        for (int index = 1; index < 50; index++) {
            String node = env.getProperty(String.format("%s[%d]", prefix, index), "");
            if (StringUtils.isBlank(node)) {
                // 配置结束，跳出
                break;
            }
            String[] segs = StringUtils.split(node, ":", true, true);
            if (segs.length != 2) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The host and ports configuration[%s] invalid, it will be ignored.", node));
                }
                continue;
            }
            nodes.add(new RedisNode(segs[0], Integer.valueOf(segs[1])));
        }
        return nodes;
    }

    public RedisSentinelConfiguration getSentinelConfig() {
        RedisSentinelConfiguration config = new RedisSentinelConfiguration();
        config.setDatabase(sentinelDatabase);
        config.setMaster(sentinelMaster);
        config.setPassword(RedisPassword.of(sentinelPassword));
        config.setSentinels(getNodes("redis.sentinel.sentinelHostAndPorts"));
        return config;
    }

    public RedisClusterConfiguration getClusterConfig() {
        RedisClusterConfiguration config = new RedisClusterConfiguration();
        config.setMaxRedirects(clusterRedirects);
        config.setPassword(RedisPassword.of(clusterPassword));
        config.setClusterNodes(getNodes("redis.cluster.clusterHostAndPorts"));
        return config;
    }
}

package org.mx.spring.redis;

import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 描述： Redis连接工厂类
 *
 * @author John.Peng
 * Date time 2018/4/25 下午2:22
 */
public class MyRedisConnectionFactoryBean {
    private JedisConnectionFactory connectionFactory = null;
    private RedisConfigBean config;

    /**
     * 默认的构造函数
     *
     * @param config Redis配置对象
     */
    public MyRedisConnectionFactoryBean(RedisConfigBean config) {
        super();
        this.config = config;
    }

    /**
     * 销毁连接工厂Bean
     */
    public void destroy() {
        connectionFactory.destroy();
    }

    /**
     * 初始化连接工厂Bean
     */
    public void init() {
        boolean enable = config.isEnable();
        if (enable) {
            String redisType = config.getType();
            JedisPoolConfig poolConfig = config.getPoolConfig();
            switch (redisType) {
                case "standalone":
                    connectionFactory = new JedisConnectionFactory(config.getStandaloneConfig());
                    break;
                case "sentinel":
                    connectionFactory = new JedisConnectionFactory(config.getSentinelConfig(), poolConfig);
                    break;
                case "cluster":
                    connectionFactory = new JedisConnectionFactory(config.getClusterConfig(), poolConfig);
                default:
                    throw new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SPRING_CACHE_REDIS_TYPE_UNSUPPORTED);
            }
            connectionFactory.afterPropertiesSet();
        }
    }

    /**
     * 获取Redis连接工厂对象
     *
     * @return Redis连接工厂对象
     */
    public RedisConnectionFactory getRedisConnectionFactory() {
        return connectionFactory;
    }
}

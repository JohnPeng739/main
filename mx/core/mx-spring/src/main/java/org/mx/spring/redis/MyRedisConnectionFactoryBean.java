package org.mx.spring.redis;

import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * 描述： Redis连接工厂类
 *
 * @author John.Peng
 * Date time 2018/4/25 下午2:22
 */
@Component("myRedisConnectionFactoryBean")
public class MyRedisConnectionFactoryBean implements InitializingBean, DisposableBean {
    private Environment env;

    private JedisConnectionFactory connectionFactory = null;

    /**
     * 默认的构造函数
     *
     * @param env Spring环境上下文
     */
    @Autowired
    public MyRedisConnectionFactoryBean(Environment env) {
        super();
        this.env = env;
    }

    /**
     * {@inheritDoc}
     *
     * @see DisposableBean#destroy()
     */
    @Override
    public void destroy() throws Exception {
        connectionFactory.destroy();
    }

    /**
     * {@inheritDoc}
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        boolean enable = env.getProperty("redis.enable", Boolean.class, false);
        if (enable) {
            String redisType = env.getProperty("redis.type");
            RedisPoolConfig poolConfig = new RedisPoolConfig(env);
            switch (redisType) {
                case "standalone":
                    RedisStandaloneConfig standaloneConfig = new RedisStandaloneConfig(env);
                    connectionFactory = new JedisConnectionFactory(standaloneConfig.get());
                    break;
                case "sentinel":
                    RedisSentinelConfig sentinelConfig = new RedisSentinelConfig(env);
                    connectionFactory = new JedisConnectionFactory(sentinelConfig.get(), poolConfig.get());
                    break;
                case "cluster":
                    RedisClusterConfig clusterConfig = new RedisClusterConfig(env);
                    connectionFactory = new JedisConnectionFactory(clusterConfig.get(), poolConfig.get());
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

package org.mx.spring.config;

import org.mx.spring.redis.MyRedisConnectionFactoryBean;
import org.mx.spring.redis.RedisConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 描述： Redis配置类
 *
 * @author John.Peng
 * Date time 2018/4/25 下午2:39
 */
public class SpringRedisConfig {

    /**
     * 创建redis配置对象
     *
     * @param env Spring IoC上下文环境
     * @return redis配置对象
     */
    @Bean
    public RedisConfigBean redisConfigBean(Environment env) {
        return new RedisConfigBean(env);
    }

    /**
     * 创建Redis连接工厂Bean
     *
     * @param redisConfigBean Redis配置对象
     * @return Redis连接工厂Bean
     */
    @Bean(name = "myRedisConnectionFactoryBean", initMethod = "init", destroyMethod = "destroy")
    public MyRedisConnectionFactoryBean myRedisConnectionFactoryBean(RedisConfigBean redisConfigBean) {
        return new MyRedisConnectionFactoryBean(redisConfigBean);
    }

    /**
     * 根据Redis连接工厂Bean创建RedisTemplate
     *
     * @param connectionFactoryBean Redis连接工厂Bean
     * @return RedisTemplate
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<?, ?> redisTemplate(MyRedisConnectionFactoryBean connectionFactoryBean) {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        RedisConnectionFactory connectionFactory = connectionFactoryBean.getRedisConnectionFactory();
        if (connectionFactory == null) {
            throw new IllegalArgumentException("You maybe not config the redis connection.");
        }
        redisTemplate.setConnectionFactory(connectionFactoryBean.getRedisConnectionFactory());
        return redisTemplate;
    }
}

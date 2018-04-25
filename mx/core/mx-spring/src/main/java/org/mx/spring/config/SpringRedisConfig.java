package org.mx.spring.config;

import org.mx.spring.redis.MyRedisConnectionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 描述： Redis配置类
 *
 * @author John.Peng
 *         Date time 2018/4/25 下午2:39
 */
@PropertySource({
        "classpath:redis.properties"
})
@ComponentScan({
        "org.mx.spring.redis"
})
public class SpringRedisConfig {
    @Autowired
    private MyRedisConnectionFactoryBean connectionFactoryBean = null;

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactoryBean.getRedisConnectionFactory());
        return redisTemplate;
    }
}

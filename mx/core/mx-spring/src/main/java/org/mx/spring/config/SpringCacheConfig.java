package org.mx.spring.config;

import org.mx.spring.cache.CacheManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * 描述： 使用Spring Cache的配置定义
 *
 * @author John.Peng
 *         Date time 2018/4/22 下午7:02
 */
@EnableCaching
@PropertySource({
        "classpath:cache.properties"
})
@ComponentScan({
        "org.mx.spring.cache"
})
public class SpringCacheConfig {
    @Autowired
    private CacheManagerFactory cacheManagerFactory = null;

    @Bean("cacheManager")
    @DependsOn("cacheManagerFactory")
    public CacheManager cacheManager() {
        return cacheManagerFactory.getCacheManager();
    }
}

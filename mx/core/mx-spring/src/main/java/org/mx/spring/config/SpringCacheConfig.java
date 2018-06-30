package org.mx.spring.config;

import org.mx.spring.cache.CacheManagerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * 描述： 使用Spring Cache的配置定义
 *
 * @author John.Peng
 * Date time 2018/4/22 下午7:02
 */
@EnableCaching
public class SpringCacheConfig {
    /**
     * 创建缓存管理器工厂
     *
     * @param context Spring IoC上下文
     * @param env     Spring上下文环境
     * @return 缓存管理器工厂
     */
    @Bean(value = "cacheManagerFactory", initMethod = "init", destroyMethod = "destroy")
    public CacheManagerFactory cacheManagerFactory(ApplicationContext context, Environment env) {
        return new CacheManagerFactory(context, env);
    }

    /**
     * 传教缓存管理器
     *
     * @param cacheManagerFactory 缓存管理器工厂
     * @return 缓存管理器
     */
    @Bean("cacheManager")
    public CacheManager cacheManager(CacheManagerFactory cacheManagerFactory) {
        return cacheManagerFactory.getCacheManager();
    }
}

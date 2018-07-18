package org.mx.spring.config;

import org.mx.spring.cache.CacheConfigBean;
import org.mx.spring.cache.CacheManagerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 描述： 使用Spring Cache的配置定义
 *
 * @author John.Peng
 * Date time 2018/4/22 下午7:02
 */
@EnableCaching
public class SpringCacheConfig {

    /**
     * 创建缓存配置对象
     *
     * @return 缓存配置对象
     */
    @Bean
    public CacheConfigBean cacheConfigBean() {
        return new CacheConfigBean();
    }

    /**
     * 创建缓存管理器工厂
     *
     * @param context Spring IoC上下文
     * @param config  缓存配置对象
     * @return 缓存管理器工厂
     */
    @Bean(value = "cacheManagerFactory", initMethod = "init", destroyMethod = "destroy")
    public CacheManagerFactory cacheManagerFactory(ApplicationContext context, CacheConfigBean config) {
        return new CacheManagerFactory(context, config);
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

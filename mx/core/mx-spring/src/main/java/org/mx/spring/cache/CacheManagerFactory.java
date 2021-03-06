package org.mx.spring.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.spring.cache.redis.RedisCache;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述： 缓存管理器工厂类定义
 *
 * @author John.Peng
 * Date time 2018/4/22 下午7:07
 */
public class CacheManagerFactory {
    private static final Log logger = LogFactory.getLog(CacheManagerFactory.class);

    private ApplicationContext context;
    private CacheConfigBean config;

    private CacheManager cacheManager = null;
    private DisposableBean disposableBean = null;

    /**
     * 默认的构造函数
     *
     * @param context Spring IoC上下文
     * @param config  缓存配置对象
     */
    public CacheManagerFactory(ApplicationContext context, CacheConfigBean config) {
        super();
        this.context = context;
        this.config = config;
    }

    /**
     * 初始化缓存工厂
     */
    public void init() {
        String type = config.getType();
        switch (type) {
            case "ehcache":
                createEhCacheManager();
                break;
            case "redis":
                createRedisCacheManager();
                break;
            case "internal":
                createConcurrentMapCacheManager();
                break;
            default:
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Unsupported type[%s], will create a internal cache.", type));
                }
                createConcurrentMapCacheManager();
                break;
        }
    }

    /**
     * 创建基于ConcurrentMap的缓存
     */
    private void createConcurrentMapCacheManager() {
        String[] names = config.getInternalList();
        Set<ConcurrentMapCache> caches = new HashSet<>(names.length);
        for (String name : names) {
            caches.add(new ConcurrentMapCache(name));
        }
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(caches);
        cacheManager = simpleCacheManager;
    }

    /**
     * 创建基于ehcache的缓存
     */
    private void createEhCacheManager() {
        String location = config.getEhcacheConfig();
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation(new ClassPathResource(location));
        factoryBean.afterPropertiesSet();
        cacheManager = new EhCacheCacheManager(factoryBean.getObject());
        disposableBean = factoryBean;
    }

    /**
     * 创建基于redis的缓存
     */
    @SuppressWarnings("unchecked")
    private void createRedisCacheManager() {
        String[] names = config.getRedisList();
        Set<RedisCache> caches = new HashSet<>(names.length);
        RedisTemplate<String, Object> redisTemplate = context.getBean(RedisTemplate.class);
        for (String name : names) {
            RedisCache redisCache = new RedisCache();
            redisCache.setName(name);
            redisCache.setRedisTemplate(redisTemplate);
            caches.add(redisCache);
        }
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(caches);
        cacheManager = simpleCacheManager;
    }

    /**
     * 销毁缓存工厂
     */
    public void destroy() {
        if (disposableBean != null) {
            try {
                disposableBean.destroy();
                disposableBean = null;
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Destroy the cache manager factory fail.", ex);
                }
            }
        }
    }

    /**
     * 获取配置好的缓存管理器
     *
     * @return 缓存管理器
     */
    public CacheManager getCacheManager() {
        return cacheManager;
    }
}

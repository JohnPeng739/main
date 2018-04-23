package org.mx.spring.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述： 缓存管理器工厂类定义
 *
 * @author John.Peng
 *         Date time 2018/4/22 下午7:07
 */
public class CacheManagerFactory {
    private static final Log logger = LogFactory.getLog(CacheManagerFactory.class);

    private Environment env = null;

    private CacheManager cacheManager = null;

    private DisposableBean disposableBean = null;

    public void setEnvironment(Environment env) {
        this.env = env;
    }

    public void init() {
        String type = env.getProperty("cache.type");
        switch (type) {
            case "memcache":
                // TODO memcache
                break;
            case "ehcache":
                createEhCacheManager();
                break;
            case "redis":
                // TODO redis
                break;
            case "jcache":
                // TODO jcache
                break;
            case "internal":
            default:
                createConcurrentMapCacheManager();
                break;
        }
    }

    // 创建基于ConcurrentMap的缓存
    private void createConcurrentMapCacheManager() {
        String list = env.getProperty("cache.internal.list", "");
        String[] names = StringUtils.split(list);
        Set<ConcurrentMapCache> caches = new HashSet<>(names.length);
        for (String name : names) {
            caches.add(new ConcurrentMapCache(name));
        }
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(caches);
        cacheManager = simpleCacheManager;
    }

    // 创建基于ehcache的缓存
    private void createEhCacheManager() {
        String location = env.getProperty("cache.ehcache.config", "ehcache.xml");
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation(new ClassPathResource(location));
        factoryBean.afterPropertiesSet();
        cacheManager = new EhCacheCacheManager(factoryBean.getObject());
        disposableBean = factoryBean;
    }

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

    public CacheManager getCacheManager() {
        return cacheManager;
    }
}

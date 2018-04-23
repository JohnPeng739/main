package org.mx.spring.cache.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.spring.cache.service.TestCacheService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@CacheConfig(cacheNames = "test1")
public class TestCacheServiceImpl implements TestCacheService {
    private static final Log logger = LogFactory.getLog(TestCacheServiceImpl.class);

    private Map<String, String> cache = new HashMap<>();

    @Cacheable(key = "#id")
    @Override
    public String getStringById(String id) {
        logger.debug(String.format("Enter getStringById(%s)", id));
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        return null;
    }

    @CachePut(key = "#id")
    @Override
    public String putStringById(String id, String content) {
        logger.debug(String.format("Enter putStringById(%s, %s)", id, content));
        content = String.format("%s - %s", id, content);
        cache.put(id, content);
        return content;
    }

    @CacheEvict(key = "#id")
    @Override
    public void delStringById(String id) {
        logger.debug(String.format("Enter delStringById(%s)", id));
        if (cache.containsKey(id)) {
            cache.remove(id);
        }
    }
}

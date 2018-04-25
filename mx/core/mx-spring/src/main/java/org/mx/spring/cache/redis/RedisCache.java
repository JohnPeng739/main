package org.mx.spring.cache.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.SerializationUtils;

import java.util.concurrent.Callable;

/**
 * 描述： 基于Redis的缓存实现，依赖Jedis。
 *
 * @author John.Peng
 *         Date time 2018/4/24 上午10:53
 */
public class RedisCache implements Cache {
    private static final Log logger = LogFactory.getLog(RedisCache.class);

    private RedisTemplate<String, Object> redisTemplate = null;
    private String name = null;

    /**
     * 获取RedisTemplate
     *
     * @return RedisTemplate
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * {@inheritDoc}
     *
     * @see Cache#getNativeCache()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 设置缓存名称
     *
     * @param name 缓存名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     *
     * @see Cache#getNativeCache()
     */
    @Override
    public Object getNativeCache() {
        return redisTemplate;
    }

    /**
     * {@inheritDoc}
     *
     * @see Cache#get(Object)
     */
    @Override
    public ValueWrapper get(Object key) {
        final String id = (String) key;
        if (StringUtils.isBlank(id)) {
            if (logger.isWarnEnabled()) {
                logger.warn("The key is blank.");
            }
            return null;
        }
        RedisCallback<Object> callback = connection -> {
            byte[] buffer = connection.get(id.getBytes());
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("The item[%s]%s exist in cache.", id,
                        (buffer == null || buffer.length <= 0 ? " not" : "")));
            }
            return buffer == null || buffer.length <= 0 ? null : SerializationUtils.deserialize(buffer);
        };
        Object value = redisTemplate.execute(callback);
        return new SimpleValueWrapper(value);
    }

    /**
     * {@inheritDoc}
     *
     * @see Cache#get(Object, Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, @Nullable Class<T> type) {
        return (T) this.get(key).get();
    }

    /**
     * {@inheritDoc}
     *
     * @see Cache#get(Object, Callable)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T) this.get(key).get();
    }

    /**
     * {@inheritDoc}
     *
     * @see Cache#put(Object, Object)
     */
    @Override
    public void put(Object key, Object value) {
        final String id = (String) key;
        if (StringUtils.isBlank(id)) {
            if (logger.isDebugEnabled()) {
                logger.debug("The key is blank.");
            }
            return;
        }
        RedisCallback<Boolean> callback = connection -> {
            byte[] buffer = SerializationUtils.serialize(value);
            boolean result = connection.set(id.getBytes(), buffer);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Set the item[%s] into cache successfully.", id));
            }
            return result;
        };
        redisTemplate.execute(callback);
    }

    /**
     * {@inheritDoc}
     *
     * @see Cache#putIfAbsent(Object, Object)
     */
    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        this.put(key, value);
        return get(key);
    }

    /**
     * {@inheritDoc}
     *
     * @see Cache#evict(Object)
     */
    @Override
    public void evict(Object key) {
        final String id = (String) key;
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Evict the cache, key: %s.", id));
        }
        RedisCallback<Long> callback = connection -> {
            long num = connection.del(id.getBytes());
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Delete %d item successfully from cache.", num));
            }
            return num;
        };
        redisTemplate.execute(callback);
    }

    /**
     * {@inheritDoc}
     *
     * @see Cache#clear()
     */
    @Override
    public void clear() {
        if (logger.isDebugEnabled()) {
            logger.debug("Start clear the cache......");
        }
        RedisCallback<Boolean> callback = connection -> {
            connection.flushDb();
            if (logger.isDebugEnabled()) {
                logger.debug("Clear the cache successfully.");
            }
            return true;
        };
        redisTemplate.execute(callback);
    }
}

package org.mx.spring.cache;

import org.mx.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * 描述： 缓存配置信息对象定义
 *
 * @author john peng
 * Date time 2018/7/17 下午4:08
 */
public class CacheConfigBean {
    @Value("${cache.type:internal}")
    private String type;
    @Value("${cache.internal.list:test1,test2}")
    private String internalList;
    @Value("${cache.ehcache.config:ehcache.xml}")
    private String ehcacheConfig;
    @Value("${cache.redis.list:test1,test2}")
    private String redisList;

    public String getType() {
        return type;
    }

    public String[] getInternalList() {
        return StringUtils.split(internalList);
    }

    public String getEhcacheConfig() {
        return ehcacheConfig;
    }

    public String[] getRedisList() {
        return StringUtils.split(redisList);
    }
}

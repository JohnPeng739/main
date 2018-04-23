package org.mx.spring.cache.service;

public interface TestCacheService {
    String getStringById(String id);

    String putStringById(String id, String content);

    void delStringById(String id);
}

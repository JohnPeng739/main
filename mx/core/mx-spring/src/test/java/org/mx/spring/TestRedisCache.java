package org.mx.spring;

import org.mx.spring.cache.service.TestCacheService;
import org.mx.spring.config.TestRedisCacheConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestRedisCache {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestRedisCacheConfig.class);
        TestCacheService service = context.getBean(TestCacheService.class);

        System.out.println(service.getStringById("1"));
        System.out.println(service.putStringById("1", "Test message"));
        System.out.println("has cache, not enter get method......");
        System.out.println(service.getStringById("1"));
        System.out.println(service.getStringById("1"));
        System.out.println(service.getStringById("1"));
        System.out.println(service.putStringById("2", "Test message"));
        System.out.println("has cache, not enter get method......");
        System.out.println(service.getStringById("2"));
        System.out.println(service.getStringById("2"));
        System.out.println(service.getStringById("2"));
        System.out.println(service.getStringById("1"));
        System.out.println(service.getStringById("1"));
        service.delStringById("1");
        System.out.println(service.getStringById("1"));
        System.out.println(service.getStringById("1"));
        System.out.println("has cache, not enter get method......");
        System.out.println(service.getStringById("1"));
        System.out.println(service.getStringById("1"));
        service.delStringById("2");
        System.out.println(service.getStringById("2"));
        System.out.println(service.getStringById("2"));

        context.close();
    }
}

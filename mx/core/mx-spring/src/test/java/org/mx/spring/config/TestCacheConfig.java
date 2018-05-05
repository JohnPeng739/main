package org.mx.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import({
        // SpringRedisConfig.class,
        SpringCacheConfig.class
})
@ComponentScan({
        "org.mx.spring.cache.service.impl"
})
public class TestCacheConfig {
}

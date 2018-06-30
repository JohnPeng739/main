package org.mx.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@PropertySource({
        "classpath:cache.properties"
})
@Import({
        SpringCacheConfig.class
})
@ComponentScan({
        "org.mx.spring.cache.service.impl"
})
public class TestCacheConfig {
}

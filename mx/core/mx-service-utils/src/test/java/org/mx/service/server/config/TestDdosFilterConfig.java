package org.mx.service.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by john on 2017/11/4.
 */
@Import(TestConfig.class)
@PropertySource({
        "classpath:server-filter-ddos.properties"
})
public class TestDdosFilterConfig {
}

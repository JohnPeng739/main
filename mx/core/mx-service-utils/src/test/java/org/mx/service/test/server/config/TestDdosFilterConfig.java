package org.mx.service.test.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by john on 2017/11/4.
 */
@Configuration
@Import(TestTcpConfig.class)
@PropertySource({
        "classpath:server-filter-ddos.properties"
})
public class TestDdosFilterConfig {
}

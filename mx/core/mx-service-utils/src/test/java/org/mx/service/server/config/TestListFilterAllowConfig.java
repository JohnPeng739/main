package org.mx.service.server.config;

import org.springframework.context.annotation.*;

/**
 * Created by john on 2017/11/4.
 */
@Configuration
@Import(TestConfig.class)
@PropertySource({
        "classpath:server-filter-list-allows.properties"
})
public class TestListFilterAllowConfig {
}

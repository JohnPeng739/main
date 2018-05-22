package org.mx.service.test.server.config;

import org.springframework.context.annotation.*;

/**
 * Created by john on 2017/11/4.
 */
@Import(TestConfig.class)
@PropertySource({
        "classpath:server-filter-list-allows.properties"
})
public class TestListFilterAllowConfig {
}

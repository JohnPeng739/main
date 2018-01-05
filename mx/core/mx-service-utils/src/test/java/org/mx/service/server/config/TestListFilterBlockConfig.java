package org.mx.service.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by john on 2017/11/4.
 */
@Configuration
@Import(TestConfig.class)
@PropertySource({
        "classpath:connect-filter-rules-block.properties"
})
public class TestListFilterBlockConfig {
}

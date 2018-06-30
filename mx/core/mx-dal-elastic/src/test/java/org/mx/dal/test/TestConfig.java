package org.mx.dal.test;

import org.mx.dal.config.DalElasticConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({
        "classpath:elastic.properties"
})
@Import({DalElasticConfig.class})
public class TestConfig {
}

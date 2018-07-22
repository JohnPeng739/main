package org.mx.dal.test;

import org.mx.dal.utils.ElasticConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource({
        "classpath:elastic.properties"
})
public class TestConfigConfig {
    @Bean(name = "elasticConfigBean")
    public ElasticConfigBean elasticConfigBean(Environment env) {
        return new ElasticConfigBean(env);
    }
}

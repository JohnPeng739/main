package org.mx.test.config;

import org.mx.hanlp.factory.SuggesterConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource({
        "classpath:suggester-test-config.properties"
})
public class TestSuggesterConfigConfig {
    @Bean
    public SuggesterConfigBean suggesterConfigBean(Environment env) {
        return new SuggesterConfigBean(env);
    }
}

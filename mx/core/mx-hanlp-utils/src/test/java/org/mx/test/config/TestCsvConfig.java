package org.mx.test.config;

import org.mx.hanlp.config.SuggesterConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({SuggesterConfig.class})
@PropertySource({
        "classpath:suggester-csv.properties"
})
public class TestCsvConfig {
}

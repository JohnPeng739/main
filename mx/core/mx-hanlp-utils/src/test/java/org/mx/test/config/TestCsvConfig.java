package org.mx.test.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@PropertySource({
        "classpath:suggester-csv.properties"
})
@ComponentScan({
        "org.mx.hanlp.factory"
})
public class TestCsvConfig {
}

package org.mx.service.test.config;

import org.mx.service.tomcat.config.TomcatServerConfig;
import org.mx.spring.config.SpringConfig;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@PropertySource({
        "classpath:cors.properties"
})
@Import({
        SpringConfig.class,
        TomcatServerConfig.class
})
public class TestTomcatConfig {
}

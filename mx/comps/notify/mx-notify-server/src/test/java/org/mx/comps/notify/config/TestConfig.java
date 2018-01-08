package org.mx.comps.notify.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;


@Configuration
@Import({NotificationServerConfig.class})
@PropertySource({"classpath:connect-filter-rules.properties"})
@ComponentScan({
        "org.mx.comps.notify.test.impl"
})
public class TestConfig {
}

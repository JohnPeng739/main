package org.mx.comps.notify.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@PropertySource({
        "classpath:server.properties"
})
@Import({NotificationServerConfig.class})
@ComponentScan({
        "org.mx.comps.notify.test.impl"
})
public class TestConfig {
}

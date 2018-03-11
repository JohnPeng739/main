package org.mx.comps.notify.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Import({NotificationServerConfig.class})
@ComponentScan({
        "org.mx.comps.notify.test.impl"
})
public class TestConfig {
}

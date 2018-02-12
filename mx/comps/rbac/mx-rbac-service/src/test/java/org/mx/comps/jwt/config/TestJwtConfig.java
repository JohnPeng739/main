package org.mx.comps.jwt.config;

import org.mx.spring.config.SpringConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@PropertySource({"classpath:/authenticate.properties"})
@Import({SpringConfig.class})
@ComponentScan({
        "org.mx.comps.rbac.rest",
        "org.mx.comps.jwt"
})
public class TestJwtConfig {
}

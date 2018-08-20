package org.mx.jwt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({
        "classpath:auth.properties"
})
@Import({
        JwtServiceConfig.class
})
public class TestJwtConfig {
}

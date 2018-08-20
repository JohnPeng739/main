package org.mx.jwt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({
        "classpath:auth-expired.properties"
})
@Import({
        JwtServiceConfig.class
})
public class TestJwtExpiredConfig {
}

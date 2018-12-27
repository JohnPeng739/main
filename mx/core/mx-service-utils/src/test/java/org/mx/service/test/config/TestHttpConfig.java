package org.mx.service.test.config;

import org.mx.jwt.config.JwtServiceConfig;
import org.mx.service.server.config.ServerConfig;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by john on 2017/11/4.
 */
@PropertySource({"classpath:server-http.properties", "classpath:auth.properties"})
@Import({ServerConfig.class, JwtServiceConfig.class})
public class TestHttpConfig {
}

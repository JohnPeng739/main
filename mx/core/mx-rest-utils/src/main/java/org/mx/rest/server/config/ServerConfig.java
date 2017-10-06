package org.mx.rest.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by john on 2017/10/6.
 */
@Configuration
@PropertySource({"classpath:server.properties"})
@ComponentScan({"org.mx.rest.server"})
public class ServerConfig {
    public ServerConfig() {
        super();
    }
}
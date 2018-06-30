package org.mx.service.test.server.config;

import org.mx.service.server.config.ServerConfig;
import org.mx.service.test.server.rest.DemoRestResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/11/4.
 */
@Import(ServerConfig.class)
@PropertySource({"classpath:server-https.properties"})
@ComponentScan({"org.mx.service.server.rest", "org.mx.service.test.server.servlet", "org.mx.service.test.server.websocket"})
public class TestHttpsConfig {
    @Bean(name = "restfulClassesTest")
    public List<Class<?>> restfulClassesTest() {
        return Arrays.asList(DemoRestResource.class);
    }
}

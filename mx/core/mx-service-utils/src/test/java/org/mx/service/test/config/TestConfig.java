package org.mx.service.test.config;

import org.mx.service.server.config.ServerConfig;
import org.mx.service.test.rest.DemoRestResource;
import org.mx.service.test.websocket.EchoWebsocket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;
import java.util.List;

@PropertySource({"classpath:server.properties"})
@Import(ServerConfig.class)
@ComponentScan({
        "org.mx.service.test.rest",
        "org.mx.service.test.websocket"
})
public class TestConfig {
    @Bean(name = "restfulClassesTest")
    public List<Class<?>> restfulClassesTest() {
        return Arrays.asList(DemoRestResource.class);
    }

    @Bean(name = "websocketClassesTest")
    public List<Class<?>> websocketClassesTest() {
        return Arrays.asList(EchoWebsocket.class);
    }
}

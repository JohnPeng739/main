package org.mx.service.test.config;

import org.mx.service.server.config.ServerConfig;
import org.mx.service.test.websocket.EchoWebsocket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/11/4.
 */
@PropertySource({"classpath:server-websocket-ssl.properties"})
@Import(ServerConfig.class)
@ComponentScan({
        "org.mx.service.test.websocket"
})
public class TestWebsocketSslConfig {
    @Bean(name = "websocketClassesTest")
    public List<Class<?>> websocketClassesTest() {
        return Arrays.asList(EchoWebsocket.class);
    }
}

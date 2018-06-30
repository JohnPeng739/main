package org.mx.service.test.server.config;

import org.mx.service.server.config.ServerConfig;
import org.mx.service.test.server.websocket.EchoWebsocket;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/11/4.
 */
@Configuration
@PropertySource({"classpath:server-websocket.properties"})
@Import(ServerConfig.class)
@ComponentScan({
        "org.mx.service.test.server.websocket"
})
public class TestWebsocketConfig {
    @Bean(name = "websocketClassesTest")
    public List<Class<?>> websocketClassesTest() {
        return Arrays.asList(EchoWebsocket.class);
    }
}

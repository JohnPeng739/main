package org.mx.service.test.server.config;

import org.mx.service.server.config.ServerConfig;
import org.mx.service.test.server.websocket.EchoWebsocket;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/11/4.
 */
@PropertySource({
        "classpath:server-filter-list-blocks.properties"
})
@Import(ServerConfig.class)
@ComponentScan({
        "org.mx.service.test.server.websocket"
})
public class TestListFilterBlockConfig {
    @Bean(name = "websocketClassesTest")
    public List<Class<?>> websocketClassesTest() {
        return Arrays.asList(EchoWebsocket.class);
    }
}

package org.mx.service.server.config;

import org.mx.service.server.HttpServerFactory;
import org.mx.service.server.ServletServerFactory;
import org.mx.service.server.WebsocketServerFactory;
import org.mx.service.server.websocket.WsSessionManager;
import org.mx.spring.config.SpringConfig;
import org.springframework.context.annotation.*;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

/**
 * REST服务器的Java Configure定义
 * 加载配置： classpath:server.properties
 *
 * @author : john.peng date : 2017/10/6
 */
@Import({SpringConfig.class})
@PropertySource({"classpath:server.properties"})
@ComponentScan({
        "org.mx.service.server",
        "org.mx.service.server.websocket"
})
public class ServerConfig {
}
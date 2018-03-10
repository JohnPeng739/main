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
@Configuration
@Import({SpringConfig.class})
@PropertySource({"classpath:server.properties"})
public class ServerConfig {
    /**
     * 默认的构造函数
     */
    public ServerConfig() {
        super();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public HttpServerFactory httpServerFactory() {
        return new HttpServerFactory();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public ServletServerFactory servletServerFactory() {
        return new ServletServerFactory();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @DependsOn({"wsSessionManager"})
    public WebsocketServerFactory websocketServerFactory() {
        return new WebsocketServerFactory();
    }

    @Bean(name = "wsSessionManager", initMethod = "init", destroyMethod = "destroy")
    public WsSessionManager wsSessionManager() {
        return new WsSessionManager();
    }
}
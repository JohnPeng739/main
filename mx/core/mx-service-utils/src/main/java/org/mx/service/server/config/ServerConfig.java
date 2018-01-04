package org.mx.service.server.config;

import org.mx.service.server.HttpServerFactory;
import org.mx.service.server.WebsocketServerFactory;
import org.mx.service.server.ServletServerFactory;
import org.mx.service.ws.ConnectRuleFactory;
import org.mx.service.ws.ConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * REST服务器的Java Configure定义
 * 加载配置： classpath:server.properties
 *
 * @author : john.peng date : 2017/10/6
 */
@Configuration
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
    public WebsocketServerFactory websocketServerFactory() {
        return new WebsocketServerFactory();
    }

    @Bean(initMethod = "init")
    public ConnectRuleFactory connectRuleFactory() {
        return new ConnectRuleFactory();
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public ConnectionManager connectionManager() {
        return new ConnectionManager();
    }
}
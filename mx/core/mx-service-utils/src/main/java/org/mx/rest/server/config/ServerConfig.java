package org.mx.rest.server.config;

import org.mx.rest.server.HttpServerFactory;
import org.mx.rest.server.WebsocketServerFactory;
import org.mx.rest.server.ServletServerFactory;
import org.springframework.context.annotation.Bean;
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
}
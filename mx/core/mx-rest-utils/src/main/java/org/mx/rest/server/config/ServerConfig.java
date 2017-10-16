package org.mx.rest.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * REST服务器的Java Configure定义
 * 扫描： org.mx.rest.server包中的组件
 * 加载配置： classpath:server.properties
 *
 * @author : john.peng date : 2017/10/6
 */
@Configuration
@PropertySource({"classpath:server.properties"})
@ComponentScan({"org.mx.rest.server"})
public class ServerConfig {
    /**
     * 默认的构造函数
     */
    public ServerConfig() {
        super();
    }
}
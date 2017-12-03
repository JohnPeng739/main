package org.mx.rest.server.config;

import org.mx.rest.server.rest.DemoRestResource;
import org.mx.rest.server.servlet.DownloadFileServlet;
import org.mx.rest.server.websocket.EchoSocket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/11/4.
 */
@Configuration
@Import(ServerConfig.class)
@ComponentScan({"org.mx.rest.server.rest", "org.mx.rest.server.servlet", "org.mx.rest.server.websocket"})
public class TestConfig {
    @Bean(name = "restfulClassesTest")
    public List<Class<?>> restfulClassesTest() {
        return Arrays.asList(DemoRestResource.class);
    }

    @Bean(name = "servletClassesTest")
    public List<Class<?>> servletClassesTest() {
        return Arrays.asList(DownloadFileServlet.class);
    }

    @Bean(name = "websocketClassesTest")
    public List<Class<?>> websocketClassesTest() {
        return Arrays.asList(EchoSocket.class);
    }
}

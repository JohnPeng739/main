package org.mx.service.test.server.config;

import org.mx.service.server.config.ServerConfig;
import org.mx.service.test.server.rest.DemoRestResource;
import org.mx.service.test.server.servlet.DownloadFileServlet;
import org.mx.service.test.server.websocket.EchoWebsocket;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/11/4.
 */
@Import(ServerConfig.class)
@PropertySource({
        "classpath:server.properties"
})
@ComponentScan({
        "org.mx.service.test.server.rest",
        "org.mx.service.test.server.servlet",
        "org.mx.service.test.server.websocket",
        "org.mx.service.test.server.comm"
})
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
        return Arrays.asList(EchoWebsocket.class);
    }
}

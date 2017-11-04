package org.mx.rest.server.config;

import org.mx.rest.server.rest.DemoRestResource;
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
@ComponentScan({"org.mx.rest.server.rest", "org.mx.rest.server.websocket"})
public class TestConfig {
    @Bean(name = "restfulClassesTest")
    public List<Class<?>> restfulClassesIprm() {
        return Arrays.asList(DemoRestResource.class);
    }

    @Bean(name = "servletClassesTest")
    public List<Class<?>> servletClassesIprm() {
        // TODO 加入知识产权管理的每个Servlet类
        return Arrays.asList();
    }
}

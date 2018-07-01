package org.mx.service.test.server.config;

import org.mx.service.server.config.ServerConfig;
import org.mx.service.test.server.rest.DemoRestResource;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/11/4.
 */
@PropertySource({"classpath:server-http.properties"})
@Import(ServerConfig.class)
@ComponentScan({
        "org.mx.service.test.server.rest"
})
public class TestHttpConfig {
    @Bean(name = "restfulClassesTest")
    public List<Class<?>> restfulClassesTest() {
        return Arrays.asList(DemoRestResource.class);
    }
}

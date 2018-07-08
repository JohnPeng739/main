package org.mx.service.test.config;

import org.mx.service.server.config.ServerConfig;
import org.mx.service.test.servlet.DownloadFileServlet;
import org.springframework.context.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by john on 2017/11/4.
 */
@PropertySource({"classpath:server-servlet.properties"})
@Import(ServerConfig.class)
@ComponentScan({
        "org.mx.service.test.servlet"
})
public class TestServletConfig {
    @Bean(name = "servletClassesTest")
    public List<Class<?>> servletClassesTest() {
        return Arrays.asList(DownloadFileServlet.class);
    }
}

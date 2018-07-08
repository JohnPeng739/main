package org.mx.service.test.config;

import org.mx.service.server.config.ServerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by john on 2017/11/4.
 */
@PropertySource({"classpath:server-tcp.properties"})
@Import(ServerConfig.class)
@ComponentScan({
        "org.mx.service.test.comm"
})
public class TestTcpConfig {
}

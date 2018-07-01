package org.mx.service.test.server.config;

import org.mx.service.server.config.ServerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by john on 2017/11/4.
 */
@PropertySource({"classpath:server-udp.properties"})
@Import(ServerConfig.class)
@ComponentScan({
        "org.mx.service.test.server.comm"
})
public class TestUdpConfig {
}

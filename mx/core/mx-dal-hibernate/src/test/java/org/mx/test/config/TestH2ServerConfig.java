package org.mx.test.config;

import org.mx.h2.server.config.DatabaseServerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * 测试DAL的配置类
 *
 * @author : john.peng created on date : 2017/12/03
 */
@Configuration
@PropertySource({
        "classpath:h2-server.properties"
})
@Import(DatabaseServerConfig.class)
public class TestH2ServerConfig {
}

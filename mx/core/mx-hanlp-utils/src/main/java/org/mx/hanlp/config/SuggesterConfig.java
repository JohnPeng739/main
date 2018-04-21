package org.mx.hanlp.config;

import org.mx.service.server.config.ServerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * 描述： 推荐服务Java Config类
 *
 * @author John.Peng
 *         Date time 2018/4/20 上午8:44
 */
@Import(ServerConfig.class)
@PropertySource({
        "classpath:suggester.properties"
})
@ComponentScan({
        "org.mx.hanlp.impl",
        "org.mx.hanlp.factory",
        "org.mx.hanlp.server"
})
public class SuggesterConfig {
}

package org.mx.h2.server.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.h2.server.H2ServerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * H2数据库服务器Java Configure定义
 * 加载配置： classpath:h2-server.properties
 *
 * @author : john.peng date : 2017/10/7
 */
@Configuration
@PropertySource({"classpath:h2-server.properties"})
public class DatabaseServerConfig {
    private static final Log logger = LogFactory.getLog(DatabaseServerConfig.class);

    @Autowired
    private Environment env = null;

    /**
     * 创建H2数据库服务器工厂
     *
     * @return 工厂
     */
    @Bean(name = "h2ServerFactory", initMethod = "init", destroyMethod = "close")
    public H2ServerFactory h2ServerFactory() {
        return new H2ServerFactory(env);
    }
}

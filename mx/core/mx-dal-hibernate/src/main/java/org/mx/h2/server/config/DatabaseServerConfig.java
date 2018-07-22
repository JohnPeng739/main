package org.mx.h2.server.config;

import org.mx.h2.server.H2ServerConfigBean;
import org.mx.h2.server.H2ServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/**
 * H2数据库服务器Java Configure定义
 * 加载配置： classpath:h2-server.properties
 *
 * @author : john.peng date : 2017/10/7
 */
@PropertySource({"classpath:h2-server.properties"})
public class DatabaseServerConfig {

    @Bean
    public H2ServerConfigBean h2ServerConfigBean() {
        return new H2ServerConfigBean();
    }

    /**
     * 创建H2数据库服务器工厂
     *
     * @param h2ServerConfigBean H2数据库配置对象
     * @return 工厂
     */
    @Bean(name = "h2ServerFactory", initMethod = "init", destroyMethod = "close")
    public H2ServerFactory h2ServerFactory(H2ServerConfigBean h2ServerConfigBean) {
        return new H2ServerFactory(h2ServerConfigBean);
    }
}

package org.mx.service.tomcat.config;

import org.mx.service.rest.cors.CorsConfigBean;
import org.mx.spring.config.SpringConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * REST服务器的Java Configure定义
 * 加载配置： classpath:server.properties
 *
 * @author : john.peng date : 2017/10/6
 */
@Import({SpringConfig.class})
public class TomcatServerConfig {
    /**
     * 创建跨域配置对象
     *
     * @return 跨域配置对象
     */
    @Bean
    public CorsConfigBean corsConfigBean() {
        return new CorsConfigBean();
    }
}
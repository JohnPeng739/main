package org.mx.service.test.config;

import org.mx.service.server.CommServerConfigBean;
import org.mx.service.server.RestfulServerConfigBean;
import org.mx.service.server.ServletServerConfigBean;
import org.mx.service.server.WebsocketServerConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * 描述： 测试服务器配置对象的配置
 *
 * @author john peng
 * Date time 2018/7/19 下午1:44
 */
@Configuration
@PropertySource({
        "classpath:server-config-test.properties"
})
public class TestServerConfigConfig {
    /**
     * 创建RESTful服务配置对象
     *
     * @return RESTful服务配置对象
     */
    @Bean
    public RestfulServerConfigBean restfulServerConfigBean() {
        return new RestfulServerConfigBean();
    }

    /**
     * 创建Servlet服务配置对象
     *
     * @return Servlet服务配置对象
     */
    @Bean
    public ServletServerConfigBean servletServerConfigBean() {
        return new ServletServerConfigBean();
    }

    /**
     * 创建WebSocket配置对象
     *
     * @return WebSocket配置对象
     */
    @Bean
    public WebsocketServerConfigBean websocketServerConfigBean() {
        return new WebsocketServerConfigBean();
    }

    /**
     * 创建COMM配置对象
     *
     * @param env Spring IoC上下文环境
     * @return COMM配置对象
     */
    @Bean
    public CommServerConfigBean commServerConfigBean(Environment env) {
        return new CommServerConfigBean(env);
    }
}

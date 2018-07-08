package org.mx.service.server.config;

import org.mx.service.server.CommServerFactory;
import org.mx.service.server.RestfulServerFactory;
import org.mx.service.server.ServletServerFactory;
import org.mx.service.server.WebsocketServerFactory;
import org.mx.service.server.websocket.rule.DdosFilterRule;
import org.mx.service.server.websocket.rule.ListFilterRule;
import org.mx.spring.config.SpringConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

/**
 * REST服务器的Java Configure定义
 * 加载配置： classpath:server.properties
 *
 * @author : john.peng date : 2017/10/6
 */
@Import({SpringConfig.class})
public class ServerConfig {
    /**
     * 创建黑白名单过滤器规则
     *
     * @param env Spring IoC上下文环境
     * @return 规则
     */
    @Bean(name = "listFilterRule")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ListFilterRule listFilterRule(Environment env) {
        return new ListFilterRule(env);
    }

    /**
     * 创建DDOS过滤器规则
     *
     * @param env Spring IoC上下文环境
     * @return 规则
     */
    @Bean(name = "ddosFilterRule")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DdosFilterRule ddosFilterRule(Environment env) {
        return new DdosFilterRule(env);
    }

    /**
     * 创建基于TCP/IP通信的服务器工厂
     *
     * @param env     Spring IoC上下文环境
     * @param context Spring IoC上下文
     * @return 通信服务器工厂
     */
    @Bean(name = "commServerFactory", initMethod = "init", destroyMethod = "destroy")
    public CommServerFactory commServerFactory(Environment env, ApplicationContext context) {
        return new CommServerFactory(env, context);
    }

    /**
     * 创建RESTful服务器工厂
     *
     * @param env     Spring IoC上下文环境
     * @param context Spring IoC上下文
     * @return RESTful服务器工厂
     */
    @Bean(name = "restfulServerFactory", initMethod = "init", destroyMethod = "destroy")
    public RestfulServerFactory restfulServerFactory(Environment env, ApplicationContext context) {
        return new RestfulServerFactory(env, context);
    }

    /**
     * 创建Servlet服务器工厂
     *
     * @param env     Spring IoC上下文环境
     * @param context Spring IoC上下文
     * @return Servlet服务器工厂
     */
    @Bean(name = "servletServerFactory", initMethod = "init", destroyMethod = "destroy")
    public ServletServerFactory servletServerFactory(Environment env, ApplicationContext context) {
        return new ServletServerFactory(env, context);
    }

    /**
     * 创建Websocket服务器工厂
     *
     * @param env     Spring IoC上下文环境
     * @param context Spring IoC上下文
     * @return Websocket服务器工厂
     */
    @Bean(name = "websocketServerFactory", initMethod = "init", destroyMethod = "destroy")
    public WebsocketServerFactory websocketServerFactory(Environment env, ApplicationContext context) {
        return new WebsocketServerFactory(env, context);
    }
}
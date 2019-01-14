package org.mx.service.server.config;

import org.mx.service.rest.cors.CorsConfigBean;
import org.mx.service.rest.graphql.GraphQLConfigBean;
import org.mx.service.rest.graphql.GraphQLFactory;
import org.mx.service.server.*;
import org.mx.service.server.websocket.WsSessionManager;
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

    /**
     * 创建跨域配置对象
     *
     * @return 跨域配置对象
     */
    @Bean
    public CorsConfigBean corsConfigBean() {
        return new CorsConfigBean();
    }

    /**
     * 根据配置文件创建GraphQL配置信息对象
     *
     * @param env Spring IoC上下文环境
     * @return GraphQL配置信息对象
     */
    @Bean
    public GraphQLConfigBean graphQLConfigBean(Environment env) {
        return new GraphQLConfigBean(env);
    }

    /**
     * 根据配置创建GraphQL工厂
     *
     * @param context Spring IoC上下文
     * @return GraphQL工厂
     */
    @Bean(name = "graphQLFactory", initMethod = "init", destroyMethod = "destroy")
    public GraphQLFactory graphQLFactory(ApplicationContext context) {
        return new GraphQLFactory(context);
    }

    @Bean(name = "wsSessionManager")
    public WsSessionManager wsSessionManager(ApplicationContext context, WebsocketServerConfigBean configBean) {
        WsSessionManager manager = WsSessionManager.getManager();
        manager.init(context, configBean);
        return manager;
    }

    /**
     * 创建黑白名单过滤器规则
     *
     * @param websocketServerConfigBean WebSocket配置对象
     * @return 规则
     */
    @Bean(name = "listFilterRule")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ListFilterRule listFilterRule(WebsocketServerConfigBean websocketServerConfigBean) {
        return new ListFilterRule(websocketServerConfigBean.getWebSocketFilter());
    }

    /**
     * 创建DDOS过滤器规则
     *
     * @param websocketServerConfigBean WebSocket配置对象
     * @return 规则
     */
    @Bean(name = "ddosFilterRule")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public DdosFilterRule ddosFilterRule(WebsocketServerConfigBean websocketServerConfigBean) {
        return new DdosFilterRule(websocketServerConfigBean.getWebSocketFilter());
    }

    /**
     * 创建基于TCP/IP通信的服务器工厂
     *
     * @param context              Spring IoC上下文
     * @param commServerConfigBean COMM通信配置对象
     * @return 通信服务器工厂
     */
    @Bean(name = "commServerFactory", initMethod = "init", destroyMethod = "destroy")
    public CommServerFactory commServerFactory(ApplicationContext context, CommServerConfigBean commServerConfigBean) {
        return new CommServerFactory(context, commServerConfigBean);
    }

    /**
     * 创建RESTful服务器工厂
     *
     * @param context                 Spring IoC上下文
     * @param restfulServerConfigBean RESTful配置对象
     * @param corsConfigBean          跨域配置对象
     * @return RESTful服务器工厂
     */
    @Bean(name = "restfulServerFactory", initMethod = "init", destroyMethod = "destroy")
    public RestfulServerFactory restfulServerFactory(ApplicationContext context,
                                                     RestfulServerConfigBean restfulServerConfigBean,
                                                     CorsConfigBean corsConfigBean) {
        return new RestfulServerFactory(context, restfulServerConfigBean, corsConfigBean);
    }

    /**
     * 创建Servlet服务器工厂
     *
     * @param context                 Spring IoC上下文
     * @param servletServerConfigBean Servlet服务配置对象
     * @return Servlet服务器工厂
     */
    @Bean(name = "servletServerFactory", initMethod = "init", destroyMethod = "destroy")
    public ServletServerFactory servletServerFactory(ApplicationContext context,
                                                     ServletServerConfigBean servletServerConfigBean) {
        return new ServletServerFactory(context, servletServerConfigBean);
    }

    /**
     * 创建Websocket服务器工厂
     *
     * @param context                   Spring IoC上下文
     * @param websocketServerConfigBean WebSocket配置对象
     * @return Websocket服务器工厂
     */
    @Bean(name = "websocketServerFactory", initMethod = "init", destroyMethod = "destroy")
    public WebsocketServerFactory websocketServerFactory(ApplicationContext context,
                                                         WebsocketServerConfigBean websocketServerConfigBean) {
        return new WebsocketServerFactory(context, websocketServerConfigBean);
    }
}
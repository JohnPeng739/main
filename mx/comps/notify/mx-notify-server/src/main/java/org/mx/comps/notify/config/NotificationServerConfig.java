package org.mx.comps.notify.config;

import org.mx.comps.notify.processor.impl.NotifyCommandProcessor;
import org.mx.comps.notify.processor.impl.PingCommandProcessor;
import org.mx.comps.notify.processor.impl.RegistryCommandProcessor;
import org.mx.comps.notify.processor.impl.UnregistryCommandProcessor;
import org.mx.comps.notify.rest.NotifyServerResource;
import org.mx.comps.notify.websocket.NotificationWebsocket;
import org.mx.service.server.config.ServerConfig;
import org.mx.service.server.websocket.WsSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.List;

/**
 * 通知消息服务器配置定义
 *
 * @author : john.peng created on date : 2018/1/3
 */
@Import({ServerConfig.class})
@ComponentScan({
        "org.mx.comps.notify.processor",
        "org.mx.comps.notify.online.impl",
        "org.mx.comps.notify.websocket",
        "org.mx.comps.notify.rest"
})
public class NotificationServerConfig {

    @Bean("notifyConfigBean")
    public NotifyConfigBean notifyConfigBean() {
        return new NotifyConfigBean();
    }

    /**
     * 创建一个系统默认的终端注册命令处理器
     *
     * @param manager WebSocket会话管理器
     * @return 终端注册命令处理器
     */
    @Bean(name = "registryCommandProcessor")
    public RegistryCommandProcessor registryCommandProcessor(WsSessionManager manager) {
        return new RegistryCommandProcessor(manager);
    }

    /**
     * 创建一个系统默认的终端注销命令处理器
     *
     * @param manager WebSocket会话管理器
     * @return 终端注销命令处理器
     */
    @Bean(name = "unregistryCommandProcessor")
    public UnregistryCommandProcessor unregistryCommandProcessor(WsSessionManager manager) {
        return new UnregistryCommandProcessor(manager);
    }

    /**
     * 创建一个系统默认的终端状态报送命令处理器
     *
     * @param manager WebSocket会话管理器
     * @return 终端状态报送命令处理器
     */
    @Bean(name = "pingCommandProcessor")
    public PingCommandProcessor pingCommandProcessor(WsSessionManager manager) {
        return new PingCommandProcessor(manager);
    }

    /**
     * 创建一个系统默认的数据推送命令处理器
     *
     * @return 数据推送命令处理器
     */
    @Bean(name = "notifyCommandProcessor")
    public NotifyCommandProcessor notifyCommandProcessor() {
        return new NotifyCommandProcessor();
    }

    /**
     * 通知推送服务器提供的Restful服务列表
     *
     * @return Restful服务列表
     */
    @Bean(name = "restfulClassesNotify")
    public List<Class<?>> restfulClassesNotify() {
        return Collections.singletonList(NotifyServerResource.class);
    }

    /**
     * 通知推送服务器提供的Websocket服务列表
     *
     * @return Websocket服务列表
     */
    @Bean(name = "websocketClassesNotify")
    public List<Class<?>> websocketClassesNotify() {
        return Collections.singletonList(NotificationWebsocket.class);
    }
}

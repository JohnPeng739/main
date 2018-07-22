package org.mx.comps.notify.config;

import org.mx.comps.notify.processor.OnlineDeviceAuthenticateFactory;
import org.mx.comps.notify.processor.impl.NotifyCommandProcessor;
import org.mx.comps.notify.processor.impl.PingCommandProcessor;
import org.mx.comps.notify.processor.impl.RegistryCommandProcessor;
import org.mx.comps.notify.processor.impl.UnregistryCommandProcessor;
import org.mx.comps.notify.rest.NotifyServerResource;
import org.mx.comps.notify.websocket.NotificationWebsocket;
import org.mx.service.server.config.ServerConfig;
import org.springframework.context.ApplicationContext;
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

    @Bean
    public NotifyConfigBean notifyConfigBean() {
        return new NotifyConfigBean();
    }

    /**
     * 创建一个系统默认的终端注册命令处理器
     *
     * @return 终端注册命令处理器
     */
    @Bean(name = "registryCommandProcessor")
    public RegistryCommandProcessor registryCommandProcessor() {
        return new RegistryCommandProcessor();
    }

    /**
     * 创建一个系统默认的终端注销命令处理器
     *
     * @return 终端注销命令处理器
     */
    @Bean(name = "unregistryCommandProcessor")
    public UnregistryCommandProcessor unregistryCommandProcessor() {
        return new UnregistryCommandProcessor();
    }

    /**
     * 创建一个系统默认的终端状态报送命令处理器
     *
     * @return 终端状态报送命令处理器
     */
    @Bean(name = "pingCommandProcessor")
    public PingCommandProcessor pingCommandProcessor() {
        return new PingCommandProcessor();
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
     * 根据配置创建在线终端设备身份认证的工厂
     *
     * @param context Spring IoC上下文
     * @return 在线终端设备身份认证工厂
     */
    @Bean(name = "onlineDeviceAuthenticateFactory")
    public OnlineDeviceAuthenticateFactory onlineDeviceAuthenticateFactory(ApplicationContext context) {
        return new OnlineDeviceAuthenticateFactory(context);
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

package org.mx.comps.notify.config;

import org.mx.comps.notify.rest.NotifyServerResource;
import org.mx.comps.notify.websocket.NotificationWebsocket;
import org.mx.service.server.config.ServerConfig;
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
        "org.mx.comps.notify.websocket",
        "org.mx.comps.notify.processor",
        "org.mx.comps.notify.online.impl",
        "org.mx.comps.notify.rest"
})
public class NotificationServerConfig {
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

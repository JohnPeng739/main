package org.mx.comps.notify.config;

import org.mx.service.server.config.ServerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 通知消息服务器配置定义
 *
 * @author : john.peng created on date : 2018/1/3
 */
@Configuration
@Import({ServerConfig.class})
@ComponentScan({
        "org.mx.comps.notify.websocket",
        "org.mx.comps.notify.processor",
        "org.mx.comps.notify.online.impl"
})
public class NotificationServerConfig {
}

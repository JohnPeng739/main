package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.mx.StringUtils;
import org.mx.service.server.websocket.SimpleWsObject;
import org.mx.service.server.websocket.WsSessionListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于Jetty实现的高度定义的Websocket服务器
 *
 * @author : john.peng created on date : 2017/11/4
 */
@Component("websocketServerFactory")
public class WebsocketServerFactory extends AbstractServerFactory {
    private static final Log logger = LogFactory.getLog(WebsocketServerFactory.class);

    @Autowired
    private Environment env = null;
    @Autowired
    private ApplicationContext context = null;

    private Map<String, SimpleWsObject> socketBeans = null;

    /**
     * 默认的构造函数
     */
    public WebsocketServerFactory() {
        super();
        this.socketBeans = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        boolean enabled = env.getProperty("websocket.enabled", Boolean.class, true);
        if (!enabled) {
            // 显式配置enable为false，表示不进行初始化。
            return;
        }
        final int port = env.getProperty("websocket.port", Integer.class, 9997);
        String websocketClassesStr = "websocket.service.classes";
        String websocketServiceClassesDef = this.env.getProperty(websocketClassesStr);
        if (StringUtils.isBlank(websocketServiceClassesDef)) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("You not define [%s], will not ", websocketClassesStr));
            }
        } else {
            String[] classesDefs = websocketServiceClassesDef.split(",");
            for (String classesDef : classesDefs) {
                if (!StringUtils.isBlank(classesDef)) {
                    List<Class<?>> websocketClasses = (List) this.context.getBean(classesDef, List.class);
                    if (websocketClasses != null && !websocketClasses.isEmpty()) {
                        websocketClasses.forEach((clazz) -> {
                            WsSessionListener listener = (WsSessionListener) context.getBean(clazz);
                            socketBeans.put(listener.getPath(), new SimpleWsObject(listener));
                        });
                    }
                }
            }

            Server server = new Server(port);
            server.setHandler(new WebSocketHandler() {
                @Override
                public void configure(WebSocketServletFactory factory) {
                    factory.setCreator(new MyWebsocketCreator());
                }
            });
            server.setStopAtShutdown(true);
            server.setStopTimeout(10);
            super.setServer(server);
            server.start();
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Start Websocket server success, listen port: %d.", port));
            }
        }
    }

    public class MyWebsocketCreator implements WebSocketCreator {
        @Override
        public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
            URI requestUri = req.getRequestURI();
            if (requestUri != null) {
                String path = requestUri.getPath();
                if (!StringUtils.isBlank(path)) {
                    return socketBeans.get(path);
                }
            }
            return null;
        }
    }
}

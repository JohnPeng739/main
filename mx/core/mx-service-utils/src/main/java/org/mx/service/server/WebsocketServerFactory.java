package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.mx.StringUtils;
import org.mx.TypeUtils;
import org.mx.service.server.websocket.SimpleWsObject;
import org.mx.service.server.websocket.WsSessionListener;
import org.mx.service.server.websocket.WsSessionManager;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于Jetty实现的高度定义的Websocket服务器
 *
 * @author : john.peng created on date : 2017/11/4
 */
public class WebsocketServerFactory extends HttpServerFactory {
    private static final Log logger = LogFactory.getLog(WebsocketServerFactory.class);

    private Map<String, SimpleWsObject> socketBeans;
    private Environment env;
    private ApplicationContext context;

    /**
     * 默认的构造函数
     *
     * @param env     Spring IoC上下文环境
     * @param context Spring IoC上下文
     */
    public WebsocketServerFactory(Environment env, ApplicationContext context) {
        super(env, "websocket");
        this.socketBeans = new HashMap<>();
        this.env = env;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerFactory#getHandler()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Handler getHandler() {
        String websocketClassesStr = "websocket.service.classes";
        String websocketServiceClassesDef = env.getProperty(websocketClassesStr);
        if (StringUtils.isBlank(websocketServiceClassesDef)) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("You not define [%s], will not ", websocketClassesStr));
            }
            return null;
        } else {
            // 初始化Websocket会话管理器
            WsSessionManager.getManager().init(env, context);
            String[] classesDefs = websocketServiceClassesDef.split(",");
            for (String classesDef : classesDefs) {
                if (!StringUtils.isBlank(classesDef)) {
                    List<Class<?>> websocketClasses = (List) context.getBean(classesDef, List.class);
                    if (!websocketClasses.isEmpty()) {
                        websocketClasses.forEach((clazz) -> {
                            WsSessionListener listener = (WsSessionListener) context.getBean(clazz);
                            socketBeans.put(listener.getPath(), new SimpleWsObject(listener));
                        });
                    }
                }
            }
            return new WebSocketHandler() {
                @Override
                public void configure(WebSocketServletFactory factory) {
                    WebSocketPolicy policy = factory.getPolicy();
                    policy.setIdleTimeout(env.getProperty("websocket.idleTimeoutSecs", Long.class, 300L) * 1000);
                    policy.setAsyncWriteTimeout(env.getProperty("websocket.asyncWriteTimeoutSecs", Long.class, 30L) * 1000);
                    policy.setInputBufferSize((int) TypeUtils.string2Size(
                            env.getProperty("websocket.inputBufferSize"), 4 * 1024));
                    policy.setMaxTextMessageSize((int) TypeUtils.string2Size(
                            env.getProperty("websocket.maxTextMessageSize"), 64 * 1024));
                    policy.setMaxTextMessageBufferSize((int) TypeUtils.string2Size(
                            env.getProperty("websocket.maxTextMessageBufferSize"), 32 * 1024));
                    policy.setMaxBinaryMessageSize((int) TypeUtils.string2Size(
                            env.getProperty("websocket.maxBinaryMessageSize"), 64 * 1024));
                    policy.setMaxBinaryMessageBufferSize((int) TypeUtils.string2Size(
                            env.getProperty("websocket.maxBinaryMessageBufferSize"), 32 * 1024));
                    factory.setCreator(new MyWebsocketCreator());
                }
            };
        }
    }

    /**
     * Websocket创建器
     */
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

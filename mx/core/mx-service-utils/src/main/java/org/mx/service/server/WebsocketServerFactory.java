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
import org.mx.service.server.websocket.SimpleWsObject;
import org.mx.service.server.websocket.WsSessionListener;
import org.mx.service.server.websocket.WsSessionManager;
import org.springframework.context.ApplicationContext;

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
    private WebsocketServerConfigBean websocketServerConfigBean;
    private ApplicationContext context;

    /**
     * 默认的构造函数
     *
     * @param context                   Spring IoC上下文
     * @param websocketServerConfigBean WebSocket服务配置对象
     */
    public WebsocketServerFactory(ApplicationContext context, WebsocketServerConfigBean websocketServerConfigBean) {
        super(websocketServerConfigBean);
        this.socketBeans = new HashMap<>();
        this.context = context;
        this.websocketServerConfigBean = websocketServerConfigBean;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerFactory#getHandler()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Handler getHandler() {
        String[] classesDefs = websocketServerConfigBean.getServiceClasses();
        if (classesDefs == null || classesDefs.length <= 0) {
            if (logger.isWarnEnabled()) {
                logger.warn("You not define any WebSocket classes.");
            }
            return null;
        } else {
            // 初始化Websocket会话管理器
            WsSessionManager.getManager().init(context, websocketServerConfigBean);
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
                    policy.setIdleTimeout(websocketServerConfigBean.getIdleTimeoutSecs() * 1000);
                    policy.setAsyncWriteTimeout(websocketServerConfigBean.getAsyncWriteTimeoutSecs() * 1000);
                    policy.setInputBufferSize((int) websocketServerConfigBean.getInputBufferSize());
                    policy.setMaxTextMessageSize((int) websocketServerConfigBean.getMaxTextMessageSize());
                    policy.setMaxTextMessageBufferSize((int) websocketServerConfigBean.getMaxTextMessageBufferSize());
                    policy.setMaxBinaryMessageSize((int) websocketServerConfigBean.getMaxBinaryMessageSize());
                    policy.setMaxBinaryMessageBufferSize((int) websocketServerConfigBean.getMaxBinaryMessageBufferSize());
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
                    // 如果path没有以"/"开头，则拼上
                    if (!path.startsWith("/")) {
                        path = String.format("/%s", path);
                    }
                    return socketBeans.get(path);
                }
            }
            return null;
        }
    }
}

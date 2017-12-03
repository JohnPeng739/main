package org.mx.rest.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.mx.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于Jetty实现的高度定义的Websocket服务器
 *
 * @author : john.peng created on date : 2017/11/4
 */
public class WebsocketServerFactory extends AbstractServerFactory {
    private static final Log logger = LogFactory.getLog(WebsocketServerFactory.class);

    @Autowired
    private Environment env = null;
    @Autowired
    private ApplicationContext context = null;
    private Map<String, String> socketBeans = null;

    public WebsocketServerFactory() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @see AbstractServerFactory#init()
     */
    @Override
    public void init() throws Exception {
        boolean enabled = env.getProperty("websocket.enabled", Boolean.class, true);
        if (!enabled) {
            // 显式配置enable为false，表示不进行初始化。
            return;
        }
        final int port = env.getProperty("websocket.port", Integer.class, 9997);
        final int num = env.getProperty("websocket.num", Integer.class, 0);
        socketBeans = new HashMap<>();
        for (int index = 1; index <= num; index++) {
            String path = env.getProperty(String.format("websocket.%d.path", index));
            String socketBeanName = env.getProperty(String.format("websocket.%d.socketBeanName", index));
            socketBeans.put(path, socketBeanName);
        }
        Server server = new Server(port);
        server.setHandler(new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.setCreator(new MyWebsocketCreator());
            }
        });
        server.setStopTimeout(0);
        super.setServer(server);
        server.start();
    }

    public class MyWebsocketCreator implements WebSocketCreator {
        @Override
        public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
            URI requestUri = req.getRequestURI();
            if (requestUri != null) {
                String path = requestUri.getPath();
                if (!StringUtils.isBlank(path)) {
                    String socketBeanName = socketBeans.get(path);
                    return context.getBean(socketBeanName);
                }
            }
            return null;
        }
    }
}

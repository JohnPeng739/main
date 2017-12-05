package org.mx.service.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.extensions.Frame;

import java.io.InputStream;

/**
 * Websocket实现基类
 *
 * @author : john.peng created on date : 2017/12/03
 */
public class BaseWebsocket {
    private static final Log logger = LogFactory.getLog(BaseWebsocket.class);

    private String path = "/";

    /**
     * 默认的构造函数
     */
    public BaseWebsocket() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param path 访问路径
     */
    public BaseWebsocket(String path) {
        this();
        this.path = path;
    }

    /**
     * 获取Websocket服务的访问路径
     *
     * @return 路径
     */
    public String getPath() {
        return path;
    }

    @OnWebSocketConnect
    public void onConnection(Session session) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got connection: %s%n", session));
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Connection closed, status: %d, reason: %s.", statusCode, reason));
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        if (logger.isErrorEnabled()) {
            logger.error("Got error.", throwable);
        }
    }

    @OnWebSocketFrame
    public void onFrame(Session session, Frame frame) {
        if (logger.isDebugEnabled()) {
            logger.debug("Got a frame.");
        }
    }

    @OnWebSocketMessage
    public void onBinaryMessage(Session session, InputStream in) {
        if (logger.isDebugEnabled()) {
            logger.debug("Got a binary message.");
        }
    }

    @OnWebSocketMessage
    public void onTextMessage(Session session, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got a text message: %s.", message));
        }
    }
}

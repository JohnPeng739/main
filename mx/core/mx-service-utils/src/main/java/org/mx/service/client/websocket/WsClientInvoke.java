package org.mx.service.client.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.mx.service.error.UserInterfaceServiceErrorException;

import java.io.Serializable;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

/**
 * Websocket客户端Java调用类，支持重连机制。
 *
 * @author : john.peng created on date : 2017/12/31
 */
public class WsClientInvoke {
    private static final Log logger = LogFactory.getLog(WsClientInvoke.class);
    private final Serializable reconnectMutex = "RECONNECT_TASK";
    private WebSocketClient client = null;
    private SslContextFactory sslContextFactory = null;
    private Future<Session> connection = null;
    private String uri = null;
    private boolean reconnect = true;
    private WebsocketClientListener listener = null;
    private Timer reconnectTimer = null;

    /**
     * 初始化Websocket客户端调用器，使用HTTP方式。
     *
     * @param uri      连接Websocket服务器的URI
     * @param listener 客户端异步响应监听器，设置为null表示不监听。
     */
    public void init(final String uri, final WebsocketClientListener listener) {
        this.init(uri, listener, true);
    }

    /**
     * 初始化Websocket客户端调用器，使用HTTP方式。
     *
     * @param uri       连接Websocket服务器的URI
     * @param listener  客户端异步响应监听器，设置为null表示不监听。
     * @param reconnect 是否需要重连
     */
    public void init(final String uri, final WebsocketClientListener listener, boolean reconnect) {
        this.uri = uri;
        this.listener = listener;
        this.reconnect = reconnect;
        this.initClient();
    }

    /**
     * 初始化Websocket客户端调用器，使用HTTPS方式。
     *
     * @param uri                连接Websocket服务器的URI
     * @param listener           客户端异步响应监听器，设置为null表示不监听。
     * @param reconnect          是否需要重连
     * @param keystorePath       Keystore路径
     * @param keystorePassword   Keystore密码
     * @param keyManagerPassword Keystore管理密码
     */
    public void init(final String uri, final WebsocketClientListener listener, boolean reconnect,
                     String keystorePath, String keystorePassword, String keyManagerPassword) {
        this.sslContextFactory = new SslContextFactory();
        this.sslContextFactory.setKeyStorePath(keystorePath);
        this.sslContextFactory.setKeyStorePassword(keystorePassword);
        this.sslContextFactory.setKeyManagerPassword(keyManagerPassword);
        this.init(uri, listener, reconnect);
    }

    /**
     * 初始化Websocket客户端
     */
    private void initClient() {
        try {
            if (sslContextFactory != null) {
                // Websocket by HTTPS
                sslContextFactory.start();
                client = new WebSocketClient(sslContextFactory);
            } else {
                // Websocket by HTTP
                client = new WebSocketClient();
            }
            client.start();
            connection = client.connect(new SimpleWebsocket(), new URI(uri));
            reconnectTimer = new Timer();
            // 延迟5s后间隔15s调用
            reconnectTimer.scheduleAtFixedRate(new ReconnectTask(), 5000, 15000);
            Thread.sleep(100);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Initialize the weboscket client fail, uri: %s.", uri), ex);
            }
            throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.WS_CLIENT_INIT_FAIL);
        }
    }

    /**
     * 返回连接是否就绪
     *
     * @return 返回true表示就绪
     */
    public boolean isReady() {
        try {
            return connection != null && connection.get().isOpen();
        } catch (Exception ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Get the session's state fail.", ex);
            }
            return false;
        }
    }

    /**
     * 重连
     */
    private void reconnect() {
        synchronized (WsClientInvoke.this.reconnectMutex) {
            if (!reconnect || connection != null) {
                // 不需要重连
                return;
            }
            close();
            // 进行重连操作
            initClient();
            if (logger.isDebugEnabled()) {
                logger.debug("Websocket client reconnect successfully.");
            }
        }
    }

    /**
     * 关闭Client
     */
    private void closeClient() {
        if (connection != null) {
            try {
                connection.get().close(1000, "Normal close operate request.");
                connection.cancel(true);
                connection = null;
                if (logger.isDebugEnabled()) {
                    logger.debug("Close the connection successfully.");
                }
            } catch (Exception ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Close the connection fail.", ex);
                }
            }
        }
        if (sslContextFactory != null) {
            try {
                sslContextFactory.stop();
            } catch (Exception ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Stop the SSL context factory fail.", ex);
                }
            }
            sslContextFactory = null;
        }
        if (client != null) {
            try {
                client.stop();
                if (logger.isDebugEnabled()) {
                    logger.debug("The Websocket client is closed.");
                }
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Close the client fail.", ex);
                }
            }
        }
    }

    /**
     * 关闭Websocket客户端
     */
    public void close() {
        // 如果手动调用过close，那么强制关闭重连机制
        reconnect = false;
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
            reconnectTimer.purge();
            reconnectTimer = null;
            if (logger.isDebugEnabled()) {
                logger.debug("The reconnect task is closed.");
            }
        }
        closeClient();
    }

    /**
     * 发生一个文本消息到Websocket服务器
     *
     * @param message 文本消息
     */
    public void send(String message) {
        this.reconnect();
        try {
            connection.get().getRemote().sendString(message);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Send text message successfully, message: %s.", message));
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Send text message fail, message: %s.", message), ex);
            }
            throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.WS_SEND_FAIL);
        }
    }

    /**
     * 发生一个二进制消息到Websocket服务器
     *
     * @param message 二进制消息
     */
    public void send(byte[] message) {
        this.reconnect();
        if (message != null) {
            try {
                connection.get().getRemote().sendBytes(ByteBuffer.wrap(message));
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Send binary message successfully, message length: %d.", message.length));
                }
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Send binary message fail, length: %d.", message.length), ex);
                }
                throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.WS_SEND_FAIL);
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("The binary message is null.");
            }
        }
    }

    @WebSocket
    public class SimpleWebsocket {
        @OnWebSocketConnect
        public void onOpen(Session session) {
            if (listener != null) {
                listener.onOpen(session);
            }
        }

        @OnWebSocketMessage
        public void onMessage(Session session, String message) {
            if (listener != null) {
                listener.onTextMessage(session, message);
            }
        }

        @OnWebSocketMessage
        public void onMessage(Session session, byte[] buffer, int offset, int length) {
            if (listener != null) {
                listener.onBinaryMessage(session, buffer, offset, length);
            }
        }

        @OnWebSocketClose
        public void onClose(Session session, int code, String reason) {
            if (listener != null) {
                listener.onClose(session, code, reason);
            }
            connection = null;
        }

        @OnWebSocketError
        public void onError(Session session, Throwable ex) {
            if (listener != null) {
                listener.onError(session, ex);
            }
        }
    }

    // 重连任务类定义
    private class ReconnectTask extends TimerTask {
        /**
         * {@inheritDoc}
         *
         * @see TimerTask#run()
         */
        @Override
        public void run() {
            reconnect();
        }
    }
}

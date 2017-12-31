package org.mx.service.ws.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.mx.service.error.UserInterfaceServiceErrorException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

/**
 * Websocket客户端Java调用类
 *
 * @author : john.peng created on date : 2017/12/31
 */
public class WsClientInvoke {
    private static final Log logger = LogFactory.getLog(WsClientInvoke.class);

    private WebSocketClient client = null;

    /**
     * 初始化Websocket客户端调用器
     *
     * @param uri      连接Websocket服务器的URI
     * @param listener 客户端异步响应监听器，设置为null表示不监听。
     * @throws UserInterfaceServiceErrorException 初始化过程中发生的异常
     */
    public void init(final String uri, final BaseWebsocketClientListener listener) throws UserInterfaceServiceErrorException {
        try {
            client = new WebSocketClient(new URI(uri)) {
                /**
                 * {@inheritDoc}
                 * @see WebSocketClient#onOpen(ServerHandshake)
                 */
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    if (listener != null) {
                        listener.onOpen();
                    }
                }

                /**
                 * {@inheritDoc}
                 * @see WebSocketClient#onMessage(String)
                 */
                @Override
                public void onMessage(String message) {
                    if (listener != null) {
                        listener.onTextMessage(message);
                    }
                }

                /**
                 * {@inheritDoc}
                 * @see WebSocketClient#onMessage(ByteBuffer)
                 */
                @Override
                public void onMessage(ByteBuffer buffer) {
                    if (listener != null) {
                        listener.onBinaryMessage(buffer.array());
                    }
                }

                /**
                 * {@inheritDoc}
                 * @see WebSocketClient#onClose(int, String, boolean)
                 */
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    if (listener != null) {
                        listener.onClose(code, reason, remote);
                    }
                }

                /**
                 * {@inheritDoc}
                 * @see WebSocketClient#onError(Exception)
                 */
                @Override
                public void onError(Exception ex) {
                    if (listener != null) {
                        listener.onError(ex);
                    }
                }
            };
            client.connect();
        } catch (URISyntaxException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Initialize the weboscket client fail, uri: %s.", uri), ex);
            }
            throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.WS_CLIENT_INIT_FAIL);
        }
    }

    /**
     * 获取连接状态
     *
     * @return 状态
     * @see WebSocket.READYSTATE
     */
    public WebSocket.READYSTATE getState() {
        if (client != null) {
            return client.getReadyState();
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("The Websocket client is null.");
            }
            return WebSocket.READYSTATE.NOT_YET_CONNECTED;
        }
    }

    /**
     * 关闭Websocket客户端
     */
    public void close() {
        if (client != null) {
            client.close(0);
            if (logger.isDebugEnabled()) {
                logger.debug("The Websocket client is closed.");
            }
        }
    }

    /**
     * 发生一个文本消息到Websocket服务器
     *
     * @param message 文本消息
     */
    public void send(String message) {
        if (client != null) {
            client.send(message);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Send text message successfully, message: %s.", message));
            }
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("The Websocket client is null.");
            }
        }
    }

    /**
     * 发生一个二进制消息到Websocket服务器
     *
     * @param message 二进制消息
     */
    public void send(byte[] message) {
        if (client != null) {
            if (message != null) {
                client.send(message);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Send binary message successfully, message length: %d.", message.length));
                }
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("The binary message is null.");
                }
            }
        } else {
            if (logger.isErrorEnabled()) {
                logger.error("The Websocket client is null.");
            }
        }
    }
}

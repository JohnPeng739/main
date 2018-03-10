package org.mx.service.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.frames.PongFrame;
import org.mx.TypeUtils;
import org.mx.spring.SpringContextHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@WebSocket
public class SimpleWsObject {
    private static final Log logger = LogFactory.getLog(SimpleWsObject.class);
    private WsSessionListener listener = null;

    public SimpleWsObject() {
        super();
    }

    public SimpleWsObject(WsSessionListener listener) {
        this();
        this.listener = listener;
    }

    /**
     * 获取会话的连接关键字，形如：10.1.1.21:4582
     *
     * @param session 会话
     * @return 连接关键字
     */
    private String getConnectKey(Session session) {
        String ip = TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress());
        int port = session.getRemoteAddress().getPort();
        return String.format("%s:%d", ip, port);
    }

    @OnWebSocketConnect
    public final void onConnect(Session session) {
        String connectKey = getConnectKey(session);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("On connect: %s.", connectKey));
        }
        WsSessionManager manager = SpringContextHolder.getBean(WsSessionManager.class);
        if (manager != null) {
            manager.addSession(session);
        }
        if (listener != null) {
            listener.afterConnect(connectKey);
        }
    }

    @OnWebSocketClose
    public final void onClose(Session session, int code, String reason) {
        String connectKey = getConnectKey(session);
        if (listener != null) {
            listener.beforeClose(connectKey);
        }
        if (logger.isInfoEnabled()) {
            logger.info(String.format("On connect: %s.", connectKey));
        }
        WsSessionManager manager = SpringContextHolder.getBean(WsSessionManager.class);
        if (manager != null) {
            manager.removeSession(session);
        }
        if (listener != null) {
            listener.afterClose(connectKey, code, reason);
        }
    }

    @OnWebSocketError
    public final void onError(Session session, Throwable throwable) {
        String connectKey = getConnectKey(session);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("On error: %s.", connectKey));
        }
        if (listener != null) {
            listener.hasError(connectKey, throwable);
        }
    }

    @OnWebSocketFrame
    public final void onFrame(Session session, Frame frame) {
        String connectKey = getConnectKey(session);
        if (frame instanceof PongFrame) {
            WsSessionManager manager = SpringContextHolder.getBean(WsSessionManager.class);
            if (manager != null) {
                manager.pong(connectKey);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Pong frame: %s, payload: %s.", connectKey, ((PongFrame) frame).getPayloadAsUTF8()));
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("On frame[%s]: %s.", frame.getType().name(), connectKey));
            }
        }
    }

    @OnWebSocketMessage
    public final void onBinaryMessage(Session session, InputStream inputStream) {
        String connectKey = getConnectKey(session);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("On binary message: %s.", connectKey));
        }
        byte[] buffer = new byte[4096];
        int len;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            while ((len = inputStream.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            if (listener != null) {
                listener.hasBinary(connectKey, baos.toByteArray());
            }
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Read byte buffer fail from %s.", connectKey), ex);
            }
        }
    }

    @OnWebSocketMessage
    public final void onTextMessage(Session session, String message) {
        String connectKey = getConnectKey(session);
        if (logger.isInfoEnabled()) {
            logger.info(String.format("On text message: %s.", connectKey));
        }
        if (listener != null) {
            listener.hasText(connectKey, message);
        }
    }
}

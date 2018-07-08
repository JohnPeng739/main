package org.mx.service.client.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;

/**
 * Websocket客户端异步监听基础实现类
 *
 * @author : john.peng created on date : 2017/12/31
 */
public class DefaultWebsocketClientMoniter implements WebsocketClientListener {
    private static final Log logger = LogFactory.getLog(DefaultWebsocketClientMoniter.class);

    /**
     * {@inheritDoc}
     *
     * @see WebsocketClientListener#onOpen(Session)
     */
    @Override
    public void onOpen(Session session) {
        if (logger.isDebugEnabled()) {
            logger.debug("The websocket client is opened.");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see WebsocketClientListener#onTextMessage(Session, String)
     */
    @Override
    public void onTextMessage(Session session, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Receive a text message, message: %s.", message));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see WebsocketClientListener#onBinaryMessage(Session, byte[], int, int)
     */
    @Override
    public void onBinaryMessage(Session session, byte[] buffer, int offset, int length) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Receive a binary message, offset: %d, length: %d.", offset, length));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see WebsocketClientListener#onClose(Session, int, String)
     */
    @Override
    public void onClose(Session session, int code, String reason) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The websocket client is closed, code: %d, reason: %s.",
                    code, reason));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see WebsocketClientListener#onError(Session, Throwable)
     */
    @Override
    public void onError(Session session, Throwable ex) {
        if (logger.isErrorEnabled()) {
            logger.error("Some error.", ex);
        }
    }
}

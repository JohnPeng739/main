package org.mx.service.client.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Websocket客户端异步监听基础实现类
 *
 * @author : john.peng created on date : 2017/12/31
 */
public class BaseWebsocketClientListener {
    private static final Log logger = LogFactory.getLog(BaseWebsocketClientListener.class);

    public void onOpen() {
        if (logger.isDebugEnabled()) {
            logger.debug("The websocket client is opened.");
        }
    }

    public void onTextMessage(String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Receive a text message, message: %s.", message));
        }
    }

    public void onBinaryMessage(byte[] message) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Receive a binary message, message length: %d.", message.length));
        }
    }

    public void onClose(int code, String reason, boolean remote) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The websocket client is closed, code: %d, reason: %s, is remote: %s.",
                    code, reason, remote));
        }
    }

    public void onError(Exception ex) {
        if (logger.isErrorEnabled()) {
            logger.error("Some error.", ex);
        }
    }
}

package org.mx.service.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 描述： 默认的Websocket session生命周期监控器实现。
 *
 * @author John.Peng
 *         Date time 2018/3/10 上午10:56
 */
public class DefaultWsSessionMonitor implements WsSessionListener {
    private static final Log logger = LogFactory.getLog(DefaultWsSessionMonitor.class);

    private String path = "/";

    public DefaultWsSessionMonitor() {
        super();
    }

    public DefaultWsSessionMonitor(String path) {
        this();
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void afterConnect(String connectKey) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("After connect: %s.", connectKey));
        }
    }

    @Override
    public void beforeClose(String connectKey) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Before close: %s.", connectKey));
        }
    }

    @Override
    public void afterClose(String connectKey, int code, String reason) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("After close: %s, code: %d, reason: %s.", connectKey, code, reason));
        }
    }

    @Override
    public void hasError(String connectKey, Throwable throwable) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Has any error: %s.", connectKey), throwable);
        }
    }

    @Override
    public void hasText(String connectKey, String text) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Receive a text message: %s, message: %s.", connectKey, text));
        }
    }

    @Override
    public void hasBinary(String connectKey, byte[] buffer) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Receive a binary message: %s, total: %d bytes.", connectKey, buffer.length));
        }
    }
}

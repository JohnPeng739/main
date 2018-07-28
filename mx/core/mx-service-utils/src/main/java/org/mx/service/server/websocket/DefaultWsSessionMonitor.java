package org.mx.service.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;

/**
 * 描述： 默认的Websocket session生命周期监控器实现。
 *
 * @author John.Peng
 * Date time 2018/3/10 上午10:56
 */
public class DefaultWsSessionMonitor implements WsSessionListener {
    private static final Log logger = LogFactory.getLog(DefaultWsSessionMonitor.class);

    private String path = "/";

    /**
     * 默认的构造函数
     */
    public DefaultWsSessionMonitor() {
        super();
    }

    /**
     * 构造函数
     *
     * @param path Websocket监听路径
     */
    public DefaultWsSessionMonitor(String path) {
        this();
        this.path = path;
    }

    /**
     * {@inheritDoc}
     *
     * @see WsSessionListener#getPath()
     */
    @Override
    public String getPath() {
        if (StringUtils.isBlank(path)) {
            // 默认为根路径
            return "/";
        } else {
            // 如果没有以"/"开头，自动拼上
            return path.startsWith("/") ? path : String.format("/%s", path);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see WsSessionListener#afterClose(String, int, String)
     */
    @Override
    public void afterConnect(String connectKey) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("After connect: %s.", connectKey));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see WsSessionListener#beforeClose(String)
     */
    @Override
    public void beforeClose(String connectKey) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Before close: %s.", connectKey));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see WsSessionListener#afterClose(String, int, String)
     */
    @Override
    public void afterClose(String connectKey, int code, String reason) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("After close: %s, code: %d, reason: %s.", connectKey, code, reason));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see WsSessionListener#hasError(String, Throwable)
     */
    @Override
    public void hasError(String connectKey, Throwable throwable) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Has any error: %s.", connectKey), throwable);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see WsSessionListener#hasText(String, String)
     */
    @Override
    public void hasText(String connectKey, String text) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Receive a text message: %s, message: %s.", connectKey, text));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see WsSessionListener#hasBinary(String, byte[])
     */
    @Override
    public void hasBinary(String connectKey, byte[] buffer) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Receive a binary message: %s, total: %d bytes.", connectKey, buffer.length));
        }
    }
}

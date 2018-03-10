package org.mx.service.server.websocket;

/**
 * 描述： Websocket session生命周期接口定义
 *
 * @author John.Peng
 *         Date time 2018/3/10 上午10:55
 */
public interface WsSessionListener {
    String getPath();

    void afterConnect(String connectKey);

    void beforeClose(String connectKey);

    void afterClose(String connectKey, int code, String reason);

    void hasError(String connectKey, Throwable throwable);

    void hasText(String connectKey, String text);

    void hasBinary(String connectKey, byte[] buffer);
}

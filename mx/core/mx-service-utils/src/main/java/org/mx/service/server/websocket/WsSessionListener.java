package org.mx.service.server.websocket;

/**
 * 描述： Websocket session生命周期接口定义
 *
 * @author John.Peng
 * Date time 2018/3/10 上午10:55
 */
public interface WsSessionListener {
    /**
     * 获取连接路径
     *
     * @return 连接路径
     */
    String getPath();

    /**
     * 建立连接后回调
     *
     * @param connectKey 连接关键字
     */
    void afterConnect(String connectKey);

    /**
     * 关闭连接前回调
     *
     * @param connectKey 连接关键字
     */
    void beforeClose(String connectKey);

    /**
     * 关闭连接后回调
     *
     * @param connectKey 连接关键字
     * @param code       关闭码
     * @param reason     关闭原因
     */
    void afterClose(String connectKey, int code, String reason);

    /**
     * 截获到错误异常
     *
     * @param connectKey 连接关键字
     * @param throwable  错误异常
     */
    void hasError(String connectKey, Throwable throwable);

    /**
     * 接受到文本消息
     *
     * @param connectKey 连接关键字
     * @param text       文本消息
     */
    void hasText(String connectKey, String text);

    /**
     * 接受到二进制消息
     *
     * @param connectKey 连接关键字
     * @param buffer     二进制消息
     */
    void hasBinary(String connectKey, byte[] buffer);
}

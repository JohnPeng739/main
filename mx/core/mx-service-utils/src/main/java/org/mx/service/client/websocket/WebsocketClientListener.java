package org.mx.service.client.websocket;

import org.eclipse.jetty.websocket.api.Session;

/**
 * 描述： Websocket客户端事件监听接口
 *
 * @author john peng
 * Date time 2018/7/7 下午8:26
 */
public interface WebsocketClientListener {
    /**
     * 收到了连接信号
     *
     * @param session 连接会话
     */
    void onOpen(Session session);

    /**
     * 收到了文本消息
     *
     * @param session 连接会话
     * @param message 文本消息
     */
    void onTextMessage(Session session, String message);

    /**
     * 收到了二进制消息数据
     *
     * @param session 连接会话
     * @param buffer  二进制数据数组
     * @param offset  数据起始偏移
     * @param length  数据长度
     */
    void onBinaryMessage(Session session, byte[] buffer, int offset, int length);

    /**
     * 收到了关闭信号
     *
     * @param session 连接会话
     * @param code    错误代码，1000表示正常关闭
     * @param reason  关闭原因
     */
    void onClose(Session session, int code, String reason);

    /**
     * 发生了异常
     *
     * @param session 连接会话
     * @param ex      异常
     */
    void onError(Session session, Throwable ex);
}

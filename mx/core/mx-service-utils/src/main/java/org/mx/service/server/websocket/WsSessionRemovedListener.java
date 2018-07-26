package org.mx.service.server.websocket;

/**
 * 描述： Websocket会话被服务端清理移除的通知接口定义
 *
 * @author john peng
 * Date time 2018/7/26 下午2:45
 */
public interface WsSessionRemovedListener {
    /**
     * 连接会话被移除成功后回调
     *
     * @param connectKey 连接关键字
     */
    void sessionRemoved(String connectKey);
}

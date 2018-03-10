package org.mx.service.server.websocket;

import org.eclipse.jetty.websocket.api.Session;

/**
 * 描述： Websocket Session过滤规则接口定义
 *
 * @author John.Peng
 *         Date time 2018/3/10 下午5:07
 */
public interface WsSessionFilterRule {
    /**
     * 初始化过滤规则
     *
     * @param manager 会话管理器
     */
    void init(WsSessionManager manager);

    /**
     * 销毁过滤规则
     */
    void destroy();

    /**
     * 进行过滤判断
     *
     * @param session 待判定的会话
     * @return 返回true表示过滤，否则放行
     */
    boolean filter(Session session);
}

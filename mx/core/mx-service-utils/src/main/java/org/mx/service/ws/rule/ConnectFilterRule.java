package org.mx.service.ws.rule;

import org.eclipse.jetty.websocket.api.Session;
import org.springframework.context.ApplicationContext;

/**
 * Websocket连接过滤规则接口定义
 *
 * @author : john.peng created on date : 2018/1/4
 */
public interface ConnectFilterRule {
    /**
     * 获取规则名称
     *
     * @return 规则名称
     */
    String getName();

    /**
     * 初始化规则
     *
     * @param key     规则对应的Key，用于获取相应的配置项
     */
    void init(String key);

    /**
     * 判定指定会话是否允许连接
     *
     * @param session 会话
     * @return 允许连接返回true，否则返回false。
     */
    boolean filter(Session session);
}

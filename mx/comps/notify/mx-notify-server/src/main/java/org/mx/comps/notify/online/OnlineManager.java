package org.mx.comps.notify.online;

import org.eclipse.jetty.websocket.api.Session;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 在线管理器接口定义
 *
 * @author : john.peng created on date : 2018/1/3
 */
public interface OnlineManager {
    /**
     * 根据条件过滤在线设备列表
     *
     * @param filters 过滤条件
     * @return 在线设备列表
     */
    Set<OnlineDevice> getOnlineDevices(List<Predicate<OnlineDevice>> filters);

    /**
     * 注册设备
     *
     * @param onlineDevice 设备对象
     * @return 成功返回true，否则返回false
     */
    boolean registryDevice(OnlineDevice onlineDevice);

    /**
     * 注销设备
     *
     * @param onlineDevice 设备对象
     * @return 成功操作返回true，否则返回false
     */
    boolean unregistryDevice(OnlineDevice onlineDevice);

    /**
     * 在线设备心跳
     *
     * @param onlineDevice 设备对象
     */
    void pingDevice(OnlineDevice onlineDevice);

    /**
     * 获取指定的连接会话
     *
     * @param connectKey 连接关键字，形如：127.0.0.1:6434
     * @return 连接会话，如果会话不存在，则返回null
     */
    Session getConnectionSession(String connectKey);

    /**
     * 通过过滤条件获取在线的连接会话
     *
     * @param filter 过滤条件
     * @return 连接会话集合
     */
    Map<String, Session> getConnectionSessions(Predicate<OnlineDevice> filter);
}

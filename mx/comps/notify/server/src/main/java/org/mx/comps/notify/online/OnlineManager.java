package org.mx.comps.notify.online;

import org.eclipse.jetty.websocket.api.Session;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 在线管理器接口定义
 *
 * @author : john.peng created on date : 2018/1/3
 */
public interface OnlineManager {
    Set<OnlineDevice> getOnlineDevices(Predicate<OnlineDevice> filter);

    boolean registryDevice(OnlineDevice onlineDevice);

    boolean unregistryDevice(OnlineDevice onlineDevice);

    void pongDevice(OnlineDevice onlineDevice);

    Session getConnectionSession(String connectKey);

    Map<String, Session> getConnectionSessions(Predicate<OnlineDevice> filter);
}

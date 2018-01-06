package org.mx.comps.notify.online.impl;

import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.mx.comps.notify.online.OnlineDevice;
import org.mx.comps.notify.online.OnlineManager;
import org.mx.service.ws.ConnectionManager;
import org.mx.spring.SpringContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 一个简单的在线设备管理实现
 *
 * @author : john.peng created on date : 2018/1/5
 */
@Component("onlineManagerSimple")
public class OnlineManagerSimpleImpl implements OnlineManager {
    private final Serializable onlineDeviceMutex = "ONLINE_DEVICE";
    private ConcurrentMap<String, OnlineDevice> onlineDevices = null;

    public OnlineManagerSimpleImpl() {
        super();
        this.onlineDevices = new ConcurrentHashMap<>();
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#getOnlineDevices(Predicate<OnlineDevice>)
     */
    @Override
    public Set<OnlineDevice> getOnlineDevices(Predicate<OnlineDevice> filter) {
        return onlineDevices.values().stream().filter(filter).collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#registryDevice(OnlineDevice)
     */
    @Override
    public boolean registryDevice(OnlineDevice onlineDevice) {
        // TODO 处理终端认证
        synchronized (OnlineManagerSimpleImpl.this.onlineDeviceMutex) {
            String key = String.format("%s@%s", onlineDevice.getDeviceId(), onlineDevice.getConnectKey());
            if (onlineDevices.containsKey(key)) {
                OnlineDevice device = onlineDevices.get(key);
                device.update(onlineDevice.getState(), onlineDevice.getLastTime(), onlineDevice.getLastLongitude(),
                        onlineDevice.getLastLatitude());
                onlineDevices.put(key, device);
            } else {
                onlineDevices.put(key, onlineDevice);
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#unregistryDevice(OnlineDevice)
     */
    @Override
    public boolean unregistryDevice(OnlineDevice onlineDevice) {
        synchronized (OnlineManagerSimpleImpl.this.onlineDeviceMutex) {
            String key = String.format("%s@%s", onlineDevice.getDeviceId(), onlineDevice.getConnectKey());
            if (onlineDevices.containsKey(key)) {
                onlineDevices.remove(key);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#pongDevice(OnlineDevice)
     */
    @Override
    public void pongDevice(OnlineDevice onlineDevice) {
        synchronized (OnlineManagerSimpleImpl.this.onlineDeviceMutex) {
            String key = String.format("%s@%s", onlineDevice.getDeviceId(), onlineDevice.getConnectKey());
            if (onlineDevices.containsKey(key)) {
                OnlineDevice device = onlineDevices.get(key);
                device.update(onlineDevice.getState(), onlineDevice.getLastTime(), onlineDevice.getLastLongitude(),
                        onlineDevice.getLastLatitude());
                onlineDevices.put(key, device);
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see OnlineManager#getConnectionSessions(Predicate)
     */
    @Override
    public Map<String, Session> getConnectionSessions(Predicate<OnlineDevice> filter) {
        Map<String, Session> sessions = new HashMap<>();
        onlineDevices.values().stream().filter(filter).forEach(onlineDevice -> {
            Session session = this.getConnectionSession(onlineDevice.getConnectKey());
            sessions.put(onlineDevice.getDeviceId(), session);
        });
        return sessions;
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#getConnectionSession(String)
     */
    @Override
    public Session getConnectionSession(String connectKey) {
        if (!StringUtils.isBlank(connectKey)) {
            ConnectionManager connectionManager = SpringContextHolder.getBean(ConnectionManager.class);
            return connectionManager.getSession(connectKey);
        } else {
            return null;
        }
    }
}

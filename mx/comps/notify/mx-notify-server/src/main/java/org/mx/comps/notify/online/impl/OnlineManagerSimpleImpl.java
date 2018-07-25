package org.mx.comps.notify.online.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.mx.comps.notify.config.NotifyConfigBean;
import org.mx.comps.notify.online.OnlineDevice;
import org.mx.comps.notify.online.OnlineDeviceAuthenticate;
import org.mx.comps.notify.online.OnlineManager;
import org.mx.comps.notify.processor.OnlineDeviceAuthenticateFactory;
import org.mx.service.server.websocket.WsSessionManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 一个简单的在线设备管理实现
 *
 * @author : john.peng created on date : 2018/1/5
 */
@Component("onlineManagerSimple")
public class OnlineManagerSimpleImpl implements OnlineManager, InitializingBean, DisposableBean {
    private static final Log logger = LogFactory.getLog(OnlineManagerSimpleImpl.class);

    private final Serializable onlineDeviceMutex = "ONLINE_DEVICE";
    private ConcurrentMap<String, OnlineDevice> onlineDevices;
    private Timer cleanTimer = null;

    private NotifyConfigBean notifyConfigBean;
    private OnlineDeviceAuthenticate deviceAuthenticate;

    /**
     * 默认的构造函数
     *
     * @param notifyConfigBean 推送配置对象
     * @param factory          设备鉴别接口
     */
    @Autowired
    public OnlineManagerSimpleImpl(NotifyConfigBean notifyConfigBean, OnlineDeviceAuthenticateFactory factory) {
        super();
        this.onlineDevices = new ConcurrentHashMap<>();
        this.notifyConfigBean = notifyConfigBean;
        if (!factory.getAuthenticates().isEmpty()) {
            this.deviceAuthenticate = factory.getAuthenticates().values().iterator().next();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#getOnlineDevices(List)
     */
    @Override
    public Set<OnlineDevice> getOnlineDevices(List<Predicate<OnlineDevice>> filters) {
        if (filters != null && !filters.isEmpty()) {
            Stream<OnlineDevice> stream = onlineDevices.values().stream();
            for (Predicate<OnlineDevice> filter : filters) {
                stream = stream.filter(filter);
            }
            return stream.collect(Collectors.toSet());
        } else {
            return new HashSet<>(onlineDevices.values());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        int deviceIdleTimeoutSecs = notifyConfigBean.getDeviceIdleTimeoutSecs();
        cleanTimer = new Timer();
        // 默认检测周期为20秒
        long period = 20000;
        if (deviceIdleTimeoutSecs > 0) {
            period = deviceIdleTimeoutSecs * 1000 / 3;
        }
        cleanTimer.scheduleAtFixedRate(new CleanTask(), 5000, period);
    }

    /**
     * {@inheritDoc}
     *
     * @see DisposableBean#destroy()
     */
    @Override
    public void destroy() {
        if (cleanTimer != null) {
            cleanTimer.cancel();
            cleanTimer.purge();
            cleanTimer = null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#registryDevice(OnlineDevice)
     */
    @Override
    public boolean registryDevice(OnlineDevice onlineDevice) {
        // 处理终端认证
        if (deviceAuthenticate != null) {
            // 需要进行设备身份鉴别
            if (!deviceAuthenticate.authenticate(onlineDevice)) {
                // 设备身份鉴别失败，返回。
                return false;
            }
        }
        synchronized (OnlineManagerSimpleImpl.this.onlineDeviceMutex) {
            String key = String.format("%s@%s", onlineDevice.getDeviceId(), onlineDevice.getConnectKey());
            if (onlineDevices.containsKey(key)) {
                OnlineDevice device = onlineDevices.get(key);
                device.setRegistryTime(System.currentTimeMillis());
                device.update(onlineDevice.getState(), System.currentTimeMillis(), onlineDevice.getLastLongitude(),
                        onlineDevice.getLastLatitude());
                onlineDevices.put(key, device);
            } else {
                onlineDevice.setRegistryTime(System.currentTimeMillis());
                onlineDevice.setLastTime(System.currentTimeMillis());
                onlineDevices.put(key, onlineDevice);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Registry [%s] successfully.", key));
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
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Unregistry [%s] successfully.", key));
                }
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see OnlineManager#pingDevice(OnlineDevice)
     */
    @Override
    public void pingDevice(OnlineDevice onlineDevice) {
        synchronized (OnlineManagerSimpleImpl.this.onlineDeviceMutex) {
            String key = String.format("%s@%s", onlineDevice.getDeviceId(), onlineDevice.getConnectKey());
            if (onlineDevices.containsKey(key)) {
                OnlineDevice device = onlineDevices.get(key);
                device.update(onlineDevice.getState(), System.currentTimeMillis(), onlineDevice.getLastLongitude(),
                        onlineDevice.getLastLatitude());
                onlineDevices.put(key, device);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Pong [%s] successfully.", key));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
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
            WsSessionManager sessionManager = WsSessionManager.getManager();
            if (sessionManager != null) {
                return sessionManager.getSession(connectKey);
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("The WsSessionManager is not initialized.");
                }
            }
        }
        return null;
    }

    /**
     * 根据心跳时间来清除无效的连接设备
     */
    private void cleanInvalidDevices() {
        WsSessionManager sessionManager = WsSessionManager.getManager();
        synchronized (OnlineManagerSimpleImpl.this.onlineDeviceMutex) {
            Set<String> invalid = new HashSet<>();
            onlineDevices.forEach((k, v) -> {
                String connectKey = v.getConnectKey();
                Session session = sessionManager.getSession(connectKey);
                if (session == null || !session.isOpen()) {
                    // 连接的会话已经断开，将设备从在线设备列表中移除
                    onlineDevices.remove(k);
                }
                int deviceIdleTimeoutSecs = notifyConfigBean.getDeviceIdleTimeoutSecs();
                if (deviceIdleTimeoutSecs > 0) {
                    // 只有设置了正确的闲置时间，才使用心跳来判定设备是否在线
                    long delay = (System.currentTimeMillis() - v.getLastTime()) / 1000;
                    if (delay > deviceIdleTimeoutSecs) {
                        // 超过约定时间没有心跳，判定为无效在线设备
                        onlineDevices.remove(k);
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("The device[%s, %s] has been cleared for %d seconds without a heartbeat.",
                                    v.getDeviceId(), v.getConnectKey(), delay));
                        }
                        if (sessionManager != null) {
                            // 同时对设备的会话进行清理
                            invalid.add(v.getConnectKey());
                        }
                    }
                }
            });
            sessionManager.removeWsSessions(invalid, 4002, "Be blocked for device's ping.");
        }
    }

    private class CleanTask extends TimerTask {
        /**
         * {@inheritDoc}
         *
         * @see TimerTask#run()
         */
        @Override
        public void run() {
            cleanInvalidDevices();
        }
    }
}

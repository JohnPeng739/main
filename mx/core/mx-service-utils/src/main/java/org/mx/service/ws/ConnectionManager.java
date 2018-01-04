package org.mx.service.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.mx.TypeUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Websocket连接管理器
 *
 * @author : john.peng created on date : 2018/1/4
 */
public final class ConnectionManager {
    private static final Log logger = LogFactory.getLog(ConnectionManager.class);
    private final Serializable cleanMutex = "CLEAN_TASK";
    private ConcurrentMap<String, Session> connections = null;
    private ConcurrentMap<String, ConnectionPerIp> connectionsPerIp = null;
    private Timer cleanTimer = null;
    private int cleanPeriodMs = 30 * 1000;
    private int testCycleSec = 10, maxNumber = 30, maxIdleSec = 30;

    public ConnectionManager() {
        super();
        this.connections = new ConcurrentHashMap<>();
        this.connectionsPerIp = new ConcurrentHashMap<>();
    }

    public void setDdosParameters(int testCycleSec, int maxNumber, int maxIdleSec) {
        this.testCycleSec = testCycleSec;
        this.maxNumber = maxNumber;
        this.maxIdleSec = maxIdleSec;
    }

    public void registryConnection(Session session) {
        String ip = TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress());
        int port = session.getRemoteAddress().getPort();
        String key = String.format("%s:%d", ip, port);
        if (!connections.containsKey(key)) {
            connections.put(key, session);
        }
        if (connectionsPerIp.containsKey(ip)) {
            connectionsPerIp.compute(key, (k, v) -> v == null ? new ConnectionPerIp(ip) : v.increment());
        }
    }

    public void unregistryConnection(Session session) {
        String ip = TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress());
        int port = session.getRemoteAddress().getPort();
        String key = String.format("%s:%d", ip, port);
        if (connections.containsKey(key)) {
            connections.remove(key);
        }
        // 对于connectionsPerIp，由守护线程来进行清理。
    }

    public void confirmConnection(Session session) {
        String ip = TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress());
        int port = session.getRemoteAddress().getPort();
        String key = String.format("%s:%d", ip, port);
        if (connections.containsKey(key) && connectionsPerIp.containsKey(ip)) {
            connectionsPerIp.compute(key, (k, v) -> v.confirm());
        }
    }

    public boolean isDdos(Session session) {
        String ip = TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress());
        if (connectionsPerIp.containsKey(ip)) {
            ConnectionPerIp connect = connectionsPerIp.get(ip);
            // 判断是否存在DDOS
            // 规则：在10秒内超过20次连接请求 或 30秒内没有确认连接
            long currentTime = System.currentTimeMillis();
            if (((currentTime - connect.lastConnectTime) > (maxIdleSec * 1000) && !connect.confirmed) ||
                    (currentTime - connect.lastConnectTime) < (testCycleSec * 1000) && connect.connectNumber > maxNumber) {
                return true;
            }
        }
        return false;
    }

    public void init() {
        cleanTimer = new Timer();
        cleanTimer.scheduleAtFixedRate(new CleanTask(), cleanPeriodMs, cleanPeriodMs);
    }

    public void destroy() {
        // 断开所有连接
        if (connections != null && !connections.isEmpty()) {
            for (Session session : connections.values()) {
                session.close(0, "server shutdown.");
            }
        }
        if (cleanTimer != null) {
            cleanTimer.cancel();
            cleanTimer.purge();
        }
        cleanTimer = null;
    }

    private class CleanTask extends TimerTask {

        /**
         * {@inheritDoc}
         *
         * @see TimerTask#run()
         */
        @Override
        public void run() {
            synchronized (ConnectionManager.this.cleanMutex) {
                clean();
            }
        }

        private void clean() {
            // 清除有DDOS嫌疑的连接
            if (connectionsPerIp != null && !connectionsPerIp.isEmpty()) {
                connectionsPerIp.forEach((ip, connect) -> {
                    long currentTime = System.currentTimeMillis();
                    if (((currentTime - connect.lastConnectTime) > (maxIdleSec + 1) * 1000 && !connect.confirmed) ||
                            (currentTime - connect.lastConnectTime) < (testCycleSec * 1000) && connect.connectNumber > maxNumber) {
                        connectionsPerIp.remove(ip);
                    }
                });
            }
            // 清除无效的连接
            if (connections != null && !connections.isEmpty()) {
                connections.forEach((key, session) -> {
                    String[] segs = StringUtils.split(key, ":", true, true);
                    if (!connectionsPerIp.containsKey(segs[0])) {
                        try {
                            session.disconnect();
                        } catch (IOException ex) {
                            if (logger.isErrorEnabled()) {
                                logger.error("Disconnect remote fail.", ex);
                            }
                        }
                        connections.remove(key);
                    }
                });
            }
        }
    }

    private class ConnectionPerIp {
        private String ip;
        private long lastConnectTime;
        private int connectNumber;
        private boolean confirmed;

        private ConnectionPerIp(String ip) {
            super();
            this.ip = ip;
            this.lastConnectTime = new Date().getTime();
            this.connectNumber = 1;
            this.confirmed = false;
        }

        private ConnectionPerIp increment() {
            this.connectNumber += 1;
            return this;
        }

        private ConnectionPerIp confirm() {
            this.confirmed = true;
            return this;
        }
    }
}

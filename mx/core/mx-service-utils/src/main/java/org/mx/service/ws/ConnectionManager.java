package org.mx.service.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.TypeUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

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
    private ConcurrentSkipListSet<String> blockIps = null;
    private Timer cleanTimer = null;
    private int cleanPeriodMs = 30 * 1000;
    private int testCycleSec = 10, maxNumber = 30, maxIdleSec = 10;

    /**
     * 默认的构造函数
     */
    public ConnectionManager() {
        super();
        this.connections = new ConcurrentHashMap<>();
        this.connectionsPerIp = new ConcurrentHashMap<>();
        this.blockIps = new ConcurrentSkipListSet<>();
    }

    /**
     * 设置DDOS攻击规则检测参数
     *
     * @param testCycleSec 检测周期，单位秒，默认为10秒
     * @param maxNumber    检测周期内允许最大连接数，默认为30个
     * @param maxIdleSec   连接确认最大超时值，单位秒，默认为10秒
     */
    public void setDdosParameters(int testCycleSec, int maxNumber, int maxIdleSec) {
        this.testCycleSec = testCycleSec;
        this.maxNumber = maxNumber;
        this.maxIdleSec = maxIdleSec;
    }

    /**
     * 获取当前已经被阻断的IP地址列表
     *
     * @return 阻断IP地址列表
     */
    public Set<String> getBlockIPs() {
        return blockIps;
    }

    /**
     * 获取指定连接关键字的会话
     *
     * @param connectKey 关键字，形如：10.1.1.21:4582
     * @return 会话对象，如果会话不存在则返回null。
     */
    public Session getSession(String connectKey) {
        return connections.get(connectKey);
    }

    /**
     * 注册连接
     *
     * @param session 会话
     */
    public void registryConnection(Session session) {
        String ip = TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress());
        int port = session.getRemoteAddress().getPort();
        String key = String.format("%s:%d", ip, port);
        synchronized (ConnectionManager.this.cleanMutex) {
            if (!connections.containsKey(key)) {
                connections.put(key, session);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Registry a connection, key: %s.", key));
                }
            }
            connectionsPerIp.compute(ip, (k, v) -> v == null ? new ConnectionPerIp(ip) : v.increment());
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Registry IP: %s, number: %d.", ip, connectionsPerIp.get(ip).connectNumber));
        }
    }

    /**
     * 注销连接
     *
     * @param session 会话
     */
    public void unregistryConnection(Session session) {
        String ip = TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress());
        int port = session.getRemoteAddress().getPort();
        String key = String.format("%s:%d", ip, port);
        synchronized (ConnectionManager.this.cleanMutex) {
            if (connections.containsKey(key)) {
                connections.remove(key);
            }
        }
        // 对于connectionsPerIp，由守护线程来进行清理。
    }

    /**
     * 确认连接有效
     *
     * @param session 会话
     */
    public void confirmConnection(Session session) {
        String ip = TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress());
        int port = session.getRemoteAddress().getPort();
        String key = String.format("%s:%d", ip, port);
        synchronized (ConnectionManager.this.cleanMutex) {
            if (connections.containsKey(key) && connectionsPerIp.containsKey(ip)) {
                connectionsPerIp.compute(ip, (k, v) -> v.confirm());
            }
        }
    }

    /**
     * 阻断指定会话及其相同IP的会话
     *
     * @param session 会话
     */
    public void blockConnection(Session session) {
        try {
            String blockIp = TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress());
            int port = session.getRemoteAddress().getPort();
            session.disconnect();
            blockIps.add(blockIp);
            // 阻断并清除该IP的其他连接
            synchronized (ConnectionManager.this.cleanMutex) {
                // 清除无效的连接
                if (connections != null && !connections.isEmpty()) {
                    connections.forEach((key, blockSession) -> {
                        if (connectionsPerIp.containsKey(blockIp)) {
                            try {
                                if (logger.isDebugEnabled()) {
                                    logger.debug(String.format("block the connection: %s.", key));
                                }
                                blockSession.disconnect();
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
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The connection[%s:%d] is blocked by the rules.",
                        TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress()),
                        session.getRemoteAddress().getPort()));
            }
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Disconnect the remote[%s:%d] fail.",
                        TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress()),
                        session.getRemoteAddress().getPort()), ex);
            }
        }
    }

    /**
     * 判断会话是否涉嫌DDOS攻击
     *
     * @param session 会话
     * @return 返回true表示为可疑的DDOS攻击，否则返回false。
     */
    public boolean isDdos(Session session) {
        String ip = TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress());
        if (connectionsPerIp.containsKey(ip)) {
            ConnectionPerIp connect = connectionsPerIp.get(ip);
            // 判断是否存在DDOS
            // 规则：在10秒内超过20次连接请求 或 10秒内没有确认连接
            long currentTime = System.currentTimeMillis();
            logger.debug(String.format("in ddos judge, ip: %s:%d, time: %ds, number: %d.", ip,
                    session.getRemoteAddress().getPort(),
                    (currentTime - connect.lastConnectTime) / 1000, connect.connectNumber));
            if (((currentTime - connect.lastConnectTime) > (maxIdleSec * 1000) && !connect.confirmed) ||
                    ((currentTime - connect.lastConnectTime) < (testCycleSec * 1000) && connect.connectNumber >= maxNumber)) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Has DDOS, ip: %s:%d, time: %ds, number: %d.", ip,
                            session.getRemoteAddress().getPort(),
                            (currentTime - connect.lastConnectTime) / 1000, connect.connectNumber));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 管理器初始化方法，被Spring IoC调用
     */
    public void init() {
        cleanTimer = new Timer();
        cleanTimer.scheduleAtFixedRate(new CleanTask(), cleanPeriodMs, cleanPeriodMs);
    }

    /**
     * 管理器销毁方法，被Spring IoC调用
     */
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

    /**
     * 定时清理无效会话的任务
     */
    private class CleanTask extends TimerTask {
        private int num;

        /**
         * {@inheritDoc}
         *
         * @see TimerTask#run()
         */
        @Override
        public void run() {
            synchronized (ConnectionManager.this.cleanMutex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Start clean the invalidated session.");
                }
                num = 0;
                clean();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Clean %d sessions successfully.", num));
                }
            }
        }

        /**
         * 定时清理无效连接
         */
        private void clean() {
            // 清除有DDOS嫌疑的连接
            if (connectionsPerIp != null && !connectionsPerIp.isEmpty()) {
                connectionsPerIp.forEach((ip, connect) -> {
                    long currentTime = System.currentTimeMillis();
                    if (((currentTime - connect.lastConnectTime) > (maxIdleSec + 1) * 1000 && !connect.confirmed) ||
                            ((currentTime - connect.lastConnectTime) < (testCycleSec * 1000) && connect.connectNumber >= maxNumber)) {
                        blockIps.add(ip);
                        connectionsPerIp.remove(ip);
                    }
                });
            }
            // 清除无效的连接
            if (connections != null && !connections.isEmpty()) {
                connections.forEach((key, session) -> {
                    int index = key.lastIndexOf(":");
                    String tarKey = "";
                    if (index >= 0) {
                        tarKey = key.substring(0, index);
                    }
                    if (!connectionsPerIp.containsKey(tarKey)) {
                        try {
                            logger.warn(String.format("The connection[%s:%d] is close by clean task.",
                                    TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress()),
                                    session.getRemoteAddress().getPort()));
                            session.disconnect();
                            num++;
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

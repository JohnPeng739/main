package org.mx.service.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.mx.TypeUtils;
import org.mx.service.server.WebsocketServerConfigBean;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 描述： Websocket Session管理器<br>
 * 连接会话过滤规则可能在加载的properties配置文件中进行如下定义：<br>
 * <pre>
 *     websocket.session.filter.rules=listFilterRule,DdosFilterRule
 * </pre>
 *
 * @author John.Peng
 * Date time 2018/3/10 上午10:07
 */
public class WsSessionManager {
    private static final Log logger = LogFactory.getLog(WsSessionManager.class);
    private static final String pingCycleSecKey = "websocket.session.ping.cycleSec";
    private static final String cleanCycleSecKey = "websocket.session.clean.cycleSec";

    private static WsSessionManager manager = null;

    private final Serializable setMutex = "Set task";
    private Timer pingTimer = null, cleanTimer = null;
    private int pingCycleSec = 10, cleanCycleSec = 60;

    private ConcurrentMap<String, Session> sessions;
    private ConcurrentMap<String, PingPongTime> pongs;
    private Set<WsSessionFilterRule> rules;
    private List<WsSessionRemovedListener> removedListeners;
    private Set<String> blocks;

    /**
     * 默认的构造函数
     */
    private WsSessionManager() {
        super();
    }

    /**
     * 获取Websocket会话管理器，单例
     *
     * @return Websocket会话管理器
     */
    public static WsSessionManager getManager() {
        if (manager == null) {
            manager = new WsSessionManager();
        }
        return manager;
    }

    /**
     * 向会话移除监听回调列表中添加指定的监听器
     *
     * @param listener 会话移除监听接口
     * @return 会话移除监听接口列表
     */
    public List<WsSessionRemovedListener> addSessionRemovedListener(WsSessionRemovedListener listener) {
        this.removedListeners.add(listener);
        return this.removedListeners;
    }

    /**
     * 从会话移除监听回调列表中移除指定的监听器
     *
     * @param listener 会话移除监听接口
     * @return 会话移除监听接口列表
     */
    public List<WsSessionRemovedListener> removeSessionRemovedListener(WsSessionRemovedListener listener) {
        this.removedListeners.remove(listener);
        return this.removedListeners;
    }

    /**
     * 获取指定连接关键字的会话
     *
     * @param connectKey 连接关键字
     * @return 会话
     */
    public Session getSession(String connectKey) {
        return sessions.get(connectKey);
    }

    /**
     * 获取被阻止连接的IP列表
     *
     * @return 被阻止的IP列表
     */
    public Set<String> getBlocks() {
        return blocks;
    }

    /**
     * 获取所有的会话
     *
     * @return 所有会话
     */
    public Map<String, Session> getSessions() {
        return sessions;
    }

    /**
     * 判定是否有规则阻止该连接请求
     *
     * @param session 会话
     * @return 返回true表示阻止连接，否则返回false
     */
    private boolean block(Session session) {
        if (rules == null) {
            return false;
        }
        for (WsSessionFilterRule rule : rules) {
            if (rule.filter(session)) {
                // 只要有一个规则符合过滤条件，就阻止连接
                blocks.add(TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress()));
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The session[%s] is blocked for the %s.", getConnectKey(session),
                            rule.getClass().getName()));
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 添加一个会话
     *
     * @param session 会话
     */
    public void addSession(Session session) {
        final int BLOCK_ERROR_CODE = 4002;
        if (session != null) {
            synchronized (setMutex) {
                String connectKey = getConnectKey(session);
                if (block(session)) {
                    session.close(BLOCK_ERROR_CODE, "Be blocked for the filter rule.");
                    return;
                }
                pongs.put(connectKey, new PingPongTime());
                sessions.put(connectKey, session);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Add a websocket session[%s] successfully.", connectKey));
                }
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("The session is null.");
            }
        }
    }

    /**
     * 删除一个会话
     *
     * @param session 会话
     */
    public void removeSession(Session session) {
        if (session != null) {
            synchronized (setMutex) {
                String connectKey = getConnectKey(session);
                removeWsSession(connectKey);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Remove a websocket session[%s] successfully.", connectKey));
                }
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("The session is null.");
            }
        }
    }

    /**
     * 收到对应连接关键字的pong消息
     *
     * @param connectKey 连接关键字
     */
    public void pong(String connectKey) {
        if (!StringUtils.isBlank(connectKey)) {
            synchronized (setMutex) {
                pongs.compute(connectKey, (k, v) -> {
                    if (v != null) {
                        return v.pong();
                    } else {
                        return null;
                    }
                });
            }
        }
    }

    /**
     * 删除指定的连接
     *
     * @param connectKeys 连接关键字集合
     * @param code        关闭状态码
     * @param reason      关闭原因
     */
    public void removeWsSessions(Set<String> connectKeys, int code, String reason) {
        if (connectKeys != null && !connectKeys.isEmpty()) {
            synchronized (setMutex) {
                connectKeys.forEach(connectKey -> removeWsSession(connectKey, code, reason));
            }
        }
    }

    /**
     * 删除指定连接关键字的会话
     *
     * @param connectKey 连接关键字
     * @see #removeWsSession(String, int, String)
     */
    private void removeWsSession(String connectKey) {
        removeWsSession(connectKey, 1000, "Normal close operate request.");
    }

    /**
     * 删除指定连接关键字的会话，连接关键字形如：10.1.1.21:4582
     *
     * @param connectKey 连接关键字
     * @param code       关闭状态码
     * @param reason     关闭原因
     */
    private void removeWsSession(String connectKey, int code, String reason) {
        if (sessions != null && sessions.containsKey(connectKey)) {
            // 先清除缓存，防止在session.close()时被重复回调。
            Session session = sessions.get(connectKey);
            sessions.remove(connectKey);
            pongs.remove(connectKey);
            try {
                session.close(code, reason);
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Close the session fail.", ex);
                }
            }
            // 通知外部监听程序，移除了相应的连接
            if (removedListeners != null && !removedListeners.isEmpty()) {
                removedListeners.forEach(listener -> listener.sessionRemoved(connectKey));
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The session[%s] not exist, the remove operate be ignored.", connectKey));
            }
        }
    }

    /**
     * 获取会话的连接关键字，形如：10.1.1.21:4582
     *
     * @param session 会话
     * @return 连接关键字
     */
    private String getConnectKey(Session session) {
        String ip = TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress());
        int port = session.getRemoteAddress().getPort();
        return String.format("%s:%d", ip, port);
    }

    /**
     * 初始化会话管理器
     *
     * @param context                   Spring IoC上下文
     * @param websocketServerConfigBean WebSocket服务配置对象
     */
    public void init(ApplicationContext context, WebsocketServerConfigBean websocketServerConfigBean) {
        this.sessions = new ConcurrentHashMap<>();
        this.pongs = new ConcurrentHashMap<>();
        this.rules = new HashSet<>();
        this.blocks = new HashSet<>();
        this.removedListeners = new ArrayList<>();
        // 配置过滤规则
        if (websocketServerConfigBean != null && websocketServerConfigBean.getWebSocketFilter() != null) {
            String[] filters = websocketServerConfigBean.getWebSocketFilter().getFilters();
            if (filters != null && filters.length > 0) {
                for (String filterStr : filters) {
                    WsSessionFilterRule rule = context.getBean(filterStr, WsSessionFilterRule.class);
                    rule.init(this);
                    rules.add(rule);
                }
            }
        }

        // 获取可能配置的ping间隔时间
        if (websocketServerConfigBean != null) {
            pingCycleSec = websocketServerConfigBean.getPingCycleSec();
            cleanCycleSec = websocketServerConfigBean.getCleanCycleSec();
        }

        // 初始化ping定时任务
        pingTimer = new Timer();
        pingTimer.scheduleAtFixedRate(new PingTask(), 0, pingCycleSec * 1000);
        // 初始化清理定时任务
        cleanTimer = new Timer();
        cleanTimer.scheduleAtFixedRate(new CleanTask(), pingCycleSec * 3 * 1000, cleanCycleSec * 1000);
    }

    /**
     * 销毁会话服务器
     */
    public void destroy() {
        // 清理ping定时任务
        if (pingTimer != null) {
            pingTimer.cancel();
            pingTimer.purge();
            pingTimer = null;
        }
        if (cleanTimer != null) {
            cleanTimer.cancel();
            cleanTimer.purge();
            cleanTimer = null;
        }
        // 销毁过滤规则
        rules.forEach(WsSessionFilterRule::destroy);
        rules.clear();
        rules = null;
        // 断开所有连接
        int num = 0;
        if (sessions != null) {
            synchronized (setMutex) {
                num = sessions.size();
                sessions.keySet().forEach(this::removeWsSession);
            }
            sessions = null;
        }
        pongs.clear();
        pongs = null;
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Clean sessions successfully, total: %d.", num));
        }
    }

    private class CleanTask extends TimerTask {
        private final int PONG_ERROR_CODE = 4001;

        /**
         * {@inheritDoc}
         *
         * @see TimerTask#run()
         */
        @Override
        public void run() {
            if (sessions == null) {
                return;
            }
            Set<String> invalid = new HashSet<>();
            sessions.forEach((connectKey, session) -> {
                if (session == null) {
                    return;
                }
                PingPongTime pingPongTime = pongs.get(connectKey);
                long pingTime = pingPongTime.pingTime, pongTime = pingPongTime.pongTime;
                if (pongTime <= 0) {
                    pongTime = System.currentTimeMillis();
                }
                if (Math.abs(pongTime - pingTime) >= 3 * pingCycleSec * 1000) {
                    // 超过3个ping周期没有收到pong回应，则认为该连接已经异常中断
                    invalid.add(connectKey);
                }
            });
            removeWsSessions(invalid, PONG_ERROR_CODE, "No pong messages have been received for more than 3 cycles.");
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Clean invalid session successfully, total: %d.", invalid.size()));
            }
        }
    }

    /**
     * 记录ping和pong时间的对象
     */
    private class PingPongTime {
        private long pingTime = System.currentTimeMillis(), pongTime = 0;

        /**
         * 记录ping时间
         *
         * @return 时间对象
         */
        public PingPongTime ping() {
            this.pingTime = System.currentTimeMillis();
            return this;
        }

        /**
         * 记录pong时间
         *
         * @return 时间对象
         */
        public PingPongTime pong() {
            this.pongTime = System.currentTimeMillis();
            return this;
        }
    }

    /**
     * 定时向客户端发生ping命令的任务，默认调度周期为10秒。
     */
    private class PingTask extends TimerTask {
        /**
         * {@inheritDoc}
         *
         * @see TimerTask#run()
         */
        @Override
        public void run() {
            if (sessions == null) {
                return;
            }
            sessions.forEach((connectKey, session) -> {
                try {
                    if (session == null) {
                        return;
                    }
                    session.getRemote().sendPing(ByteBuffer.wrap(connectKey.getBytes()));
                    pongs.compute(connectKey, (k, v) -> v == null ? new PingPongTime() : v.ping());
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Send ping to session[%s] successfully.", connectKey));
                    }
                } catch (IOException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Send ping to session[%s] fail.", connectKey));
                    }
                }
            });
        }
    }
}

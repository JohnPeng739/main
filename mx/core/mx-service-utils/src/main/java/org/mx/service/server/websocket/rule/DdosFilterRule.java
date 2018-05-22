package org.mx.service.server.websocket.rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.TypeUtils;
import org.mx.service.server.websocket.WsSessionFilterRule;
import org.mx.service.server.websocket.WsSessionManager;
import org.springframework.core.env.Environment;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 描述： DDOS攻击过滤规则<br>
 * DDOS攻击判定规则可能在加载的properties配置文件中进行如下定义：<br>
 * <pre>
 *     websocket.session.filter.rules.ddos.cycleSec=30
 *     websocket.session.filter.rules.ddos.maxConnections=15
 * </pre>
 * 如果没有配置，默认值为：判断周期=30秒，最大连接数=15<br>
 * <strong>如果一个会话被判定为DDOS攻击，仅阻止本会话连接，不影响前面已经建立的会话!</strong>
 *
 * @author John.Peng
 *         Date time 2018/3/10 下午7:46
 */
public class DdosFilterRule implements WsSessionFilterRule {
    private static final Log logger = LogFactory.getLog(DdosFilterRule.class);
    private static final String cycleSecKey = "websocket.session.filter.rules.ddos.cycleSec",
            maxConnectionsKey = "websocket.session.filter.rules.ddos.maxConnections";
    private final Serializable setMutex = "Set task";

    private Environment env = null;

    private WsSessionManager manager = null;
    private ConcurrentMap<String, Map<String, Long>> nodes = null;
    private int cycleSec = 30, maxConnections = 15;

    public DdosFilterRule() {
        super();
        nodes = new ConcurrentHashMap<>();
    }

    public DdosFilterRule(Environment env) {
        this();
        this.env = env;
    }

    @Override
    public void init(WsSessionManager manager) {
        this.manager = manager;
        cycleSec = env.getProperty(cycleSecKey, Integer.class, 30);
        maxConnections = env.getProperty(maxConnectionsKey, Integer.class, 15);
    }

    @Override
    public void destroy() {
        synchronized (setMutex) {
            nodes.clear();
        }
    }

    @Override
    public boolean filter(Session session) {
        String ip = TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress());
        int port = session.getRemoteAddress().getPort();
        String connectKey = String.format("%s:%d", ip, port);
        Map<String, Long> sessions = nodes.get(ip);
        if (sessions != null) {
            if (sessions.size() >= maxConnections) {
                // 清除可能已经无效的连接
                Map<String, Session> managedSessions = manager.getSessions();
                Set<String> invalid = new HashSet<>();
                sessions.forEach((k, v) -> {
                    if (!managedSessions.containsKey(k)) {
                        invalid.add(k);
                    }
                });
                for (String key : invalid) {
                    sessions.remove(key);
                }
                Long[] times = sessions.values().toArray(new Long[0]);
                Arrays.sort(times);
                if (times.length >= maxConnections && (times[times.length - 1] - times[0]) <= cycleSec * 1000) {
                    // 在限定的时间范围内连接次数超过了最大连接数，判定为DDOS攻击
                    return true;
                }
            }
        } else {
            sessions = new HashMap<>();
        }
        synchronized (setMutex) {
            sessions.put(connectKey, System.currentTimeMillis());
            nodes.put(ip, sessions);
        }
        return false;
    }
}

package org.mx.comps.notify.processor;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.mx.comps.notify.online.OnlineManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 通知推送处理器，这里仅进行本地推送业务，集群转发需要另外实现。
 * <p>
 * 通知的消息统一结构应如下：
 * {
 * src: "owner",
 * tarType: "devices",
 * tar: 'deviceId',
 * needAck: true,
 * message: JSONObject
 * }
 * <p>
 * 其中：tarType可以是 devices | ips | states | later | early ，分别对于tar中的设备号列表、IP列表、状态列表、晚于/早于时间(long)内容。
 *
 * @author : john.peng created on date : 2018/1/6
 */
@Component("notifyProcessor")
public class NotifyProcessor {
    private static final Log logger = LogFactory.getLog(NotifyProcessor.class);

    @Autowired
    private OnlineManager onlineManager = null;

    /**
     * 获取本机推送会话集合
     *
     * @param tarType 推送目标类型
     * @param tar     推送目标列表
     * @return 本机推送会话集合
     */
    private TarSessionSet getTarSessionSet(String tarType, String tar) {
        TarSessionSet set = new TarSessionSet();
        Map<String, Session> sessions;
        switch (tarType) {
            case "devices":
                List<String> devices = Arrays.asList(StringUtils.split(
                        tar, ",", true, true));
                sessions = onlineManager.getConnectionSessions(
                        onlineDevice -> devices.contains(onlineDevice.getDeviceId()));
                devices.forEach(device -> {
                    if (!sessions.containsKey(device)) {
                        set.maybeCluster = true;
                    }
                });
                set.sessions.addAll(sessions.values());
                return set;
            case "ips":
                List<String> ips = Arrays.asList(StringUtils.split(tar, ",", true, true));
                sessions = onlineManager.getConnectionSessions(onlineDevice -> {
                    for (String ip : ips) {
                        if (onlineDevice.getConnectKey().startsWith(ip)) {
                            return true;
                        }
                    }
                    return false;
                });
                set.maybeCluster = true;
                set.sessions.addAll(sessions.values());
                return set;
            case "states":
                List<String> states = Arrays.asList(StringUtils.split(tar, ",", true, true));
                sessions = onlineManager.getConnectionSessions(onlineDevice -> states.contains(onlineDevice.getState()));
                set.maybeCluster = true;
                set.sessions.addAll(sessions.values());
                return set;
            case "later":
                long later = Long.parseLong(tar);
                sessions = onlineManager.getConnectionSessions(onlineDevice -> onlineDevice.getLastTime() >= later);
                set.maybeCluster = true;
                set.sessions.addAll(sessions.values());
                return set;
            case "early":
                long early = Long.parseLong(tar);
                sessions = onlineManager.getConnectionSessions(onlineDevice -> onlineDevice.getLastTime() <= early);
                set.maybeCluster = true;
                set.sessions.addAll(sessions.values());
                return set;
            default:
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Unsupported type: %s.", tarType));
                }
                return null;
        }
    }

    /**
     * 通知推送
     *
     * @param set     推送会话集合
     * @param message 推送消息对象
     * @return 推送无错误返回true，否则返回false。
     */
    protected boolean notifyPush(TarSessionSet set, JSONObject message) {
        // 这里仅实现本地推送，如果需要集群通知，需要另外实现
        Set<Session> sessions = set.sessions;
        boolean success = true;
        for (Session session : sessions) {
            try {
                String text = message.toJSONString();
                session.getRemote().sendString(text);
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Push notify fail, message: %s."), ex);
                }
                success = false;
            }
        }
        return success;
    }

    /**
     * 处理通知指令
     *
     * @param data 通知数据
     */
    public final void notifyProcess(JSONObject data) {
        String src = data.getString("src");
        String tarType = data.getString("tarType");
        String tar = data.getString("tar");
        boolean needAck = data.getBooleanValue("needAck");
        JSONObject message = data.getJSONObject("message");
        TarSessionSet set = this.getTarSessionSet(tarType, tar);
        JSONObject json = new JSONObject();
        json.put("src", src);
        json.put("needAck", needAck);
        json.put("pushTime", System.currentTimeMillis());
        json.put("message", message);
        if (notifyPush(set, json)) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Push notify success, num: %d.", set.sessions.size()));
            }
        }
    }

    public class TarSessionSet {
        protected boolean maybeCluster = false;
        protected Set<Session> sessions = new HashSet<>();
    }
}

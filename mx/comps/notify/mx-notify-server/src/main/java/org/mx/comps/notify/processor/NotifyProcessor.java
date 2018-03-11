package org.mx.comps.notify.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.DateUtils;
import org.mx.StringUtils;
import org.mx.comps.notify.online.OnlineManager;
import org.mx.comps.notify.processor.impl.NotifyCommandProcessor;
import org.mx.service.server.websocket.WsSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 通知推送处理器，这里仅进行本地推送业务，集群转发需要另外实现。
 *
 * @author : john.peng created on date : 2018/1/6
 */
@Component("notifyProcessor")
public class NotifyProcessor {
    private static final Log logger = LogFactory.getLog(NotifyProcessor.class);

    @Autowired
    private OnlineManager onlineManager = null;

    @Autowired
    private WsSessionManager sessionManager = null;

    @Autowired
    private NotifyProcessListener listener = null;

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
            case "Devices":
                List<String> devices = Arrays.asList(StringUtils.split(
                        tar, ",", true, true));
                sessions = onlineManager.getConnectionSessions(
                        onlineDevice -> devices.contains(onlineDevice.getDeviceId()));
                devices.forEach(device -> {
                    if (!sessions.containsKey(device)) {
                        set.invalidDevices.add(device);
                    }
                });
                set.sessions.addAll(sessions.values());
                set.maybeCluster = !set.invalidDevices.isEmpty();
                return set;
            case "IPs":
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
            case "States":
                List<String> states = Arrays.asList(StringUtils.split(tar, ",", true, true));
                sessions = onlineManager.getConnectionSessions(onlineDevice -> states.contains(onlineDevice.getState()));
                set.maybeCluster = true;
                set.sessions.addAll(sessions.values());
                return set;
            case "Later":
                long later = Long.parseLong(tar);
                sessions = onlineManager.getConnectionSessions(onlineDevice -> onlineDevice.getRegistryTime() >= later);
                set.maybeCluster = true;
                set.sessions.addAll(sessions.values());
                return set;
            case "Early":
                long early = Long.parseLong(tar);
                sessions = onlineManager.getConnectionSessions(onlineDevice -> onlineDevice.getRegistryTime() <= early);
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
            if (session == null) {
                continue;
            }
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
        if (listener != null) {
            listener.before(data);
        }
        String src = data.getString("src");
        String deviceId = data.getString("deviceId");
        String tarType = data.getString("tarType");
        String tar = data.getString("tar");
        long expiredTime = data.getLongValue("expiredTime");
        long currentTime = System.currentTimeMillis();
        if (expiredTime > 0 && currentTime > expiredTime) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The notify command is expired, expired time: %s, current: %s.",
                        DateUtils.get23Date(new Date(expiredTime)), DateUtils.get23Date(new Date(currentTime))));
            }
            return;
        }
        JSONObject message = data.getJSONObject("message");
        TarSessionSet set = this.getTarSessionSet(tarType, tar);
        JSONObject json = new JSONObject();
        json.put("src", src);
        json.put("pushTime", System.currentTimeMillis());
        json.put("message", message);
        boolean success = notifyPush(set, json);
        if (success) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Push notify success, num: %d.", set.sessions.size()));
            }
        }
        if (sessionManager != null) {
            String connectKey = data.getString("connectKey");
            if (!StringUtils.isBlank(connectKey)) {
                // 如果没有设置connectKey，意味着来自于Restful的请求，就不需要向请求方进行反馈响应。
                Session session = sessionManager.getSession(connectKey);
                if (session != null) {
                    JSONObject res = new JSONObject();
                    res.put("srcCommand", NotifyCommandProcessor.COMMAND);
                    res.put("deviceId", deviceId);
                    res.put("status", success ? "ok" : "error");
                    res.put("error", success ? null : "Notify process has any error.");
                    try {
                        session.getRemote().sendString(JSON.toJSONString(res));
                    } catch (IOException ex) {
                        if (logger.isErrorEnabled()) {
                            logger.error(String.format("Send notfiy response message to session[%s] fail.", connectKey), ex);
                        }
                    }
                } else {
                    if (logger.isWarnEnabled()) {
                        logger.warn(String.format("The session[%s] not existed.", connectKey));
                    }
                }
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("The WsSessionManager is not initialized.");
            }
        }
        if (listener != null) {
            listener.after(data, success, set.invalidDevices);
        }
        // TODO 根据set中的maybeCluster状态，添加中继到其他集群节点中进行转发
    }

    // 内部描述推送信息的类定义
    public class TarSessionSet {
        protected boolean maybeCluster = false;
        protected Set<String> invalidDevices = new HashSet<>();
        protected Set<Session> sessions = new HashSet<>();
    }
}

package org.mx.comps.notify.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.DateUtils;
import org.mx.StringUtils;
import org.mx.comps.notify.config.NotifyConfigBean;
import org.mx.comps.notify.online.OnlineManager;
import org.mx.comps.notify.processor.impl.NotifyCommandProcessor;
import org.mx.service.server.websocket.WsSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

    private OnlineManager onlineManager;
    private WsSessionManager sessionManager;

    private NotifyProcessListener listener;

    @Autowired
    public NotifyProcessor(ApplicationContext context, NotifyConfigBean notifyConfigBean, OnlineManager onlineManager, WsSessionManager sessionManager) {
        super();
        this.onlineManager = onlineManager;
        this.sessionManager = sessionManager;
        if (!StringUtils.isBlank(notifyConfigBean.getNotifyListener())) {
            this.listener = context.getBean(notifyConfigBean.getNotifyListener(), NotifyProcessListener.class);
        }
    }

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
                String[] segs = StringUtils.split(tar, ",", true, true);
                sessions = onlineManager.getConnectionSessions(onlineDevice -> {
                    for (String ip : segs) {
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
                return set;
        }
    }

    /**
     * 通知推送
     *
     * @param set     推送会话集合
     * @param message 推送消息对象
     * @return 推送无错误返回true，否则返回false。
     */
    private boolean notifyPush(TarSessionSet set, JSONObject message) {
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
                    logger.error(String.format("Push notify fail, message: %s.", message), ex);
                }
                success = false;
            }
        }
        return success;
    }

    /**
     * 处理通知指令
     *
     * @param message 通知消息数据
     */
    public final void notifyProcess(JSONObject message) {
        notifyProcess(message, null);
    }

    /**
     * 处理通知指令
     *
     * @param message    通知消息数据
     * @param retPayload 返回给前端的负载对象
     */
    public final void notifyProcess(JSONObject message, JSONObject retPayload) {
        if (listener != null) {
            listener.before(message);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Starting notify the message: %s.", message.toJSONString()));
        }
        String deviceId = message.getString("deviceId");
        String tarType = message.getString("tarType");
        String tar = message.getString("tar");
        long expiredTime = message.getLongValue("expiredTime");
        long currentTime = System.currentTimeMillis();
        if (expiredTime > 0 && currentTime > expiredTime) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The notify command is expired, expired time: %s, current: %s.",
                        DateUtils.get23Date(new Date(expiredTime)), DateUtils.get23Date(new Date(currentTime))));
            }
            return;
        }
        TarSessionSet set = this.getTarSessionSet(tarType, tar);
        message.put("pushTime", System.currentTimeMillis());
        boolean success = notifyPush(set, message);
        if (success) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Push notify success, num: %d.", set.sessions.size()));
            }
        }

        // 如果必要，则向发送端发送推送回执
        if (sessionManager != null) {
            String connectKey = message.getString("connectKey");
            if (!StringUtils.isBlank(connectKey)) {
                // 如果没有设置connectKey，意味着来自于Restful的请求，就不需要向请求方进行反馈响应。
                Session session = sessionManager.getSession(connectKey);
                if (session != null) {
                    JSONObject res = new JSONObject();
                    res.put("srcCommand", NotifyCommandProcessor.COMMAND);
                    res.put("deviceId", deviceId);
                    res.put("status", success ? "ok" : "error");
                    res.put("error", success ? null : "Notify process has any error.");
                    if (retPayload != null) {
                        res.put("payload", retPayload);
                    }
                    try {
                        session.getRemote().sendString(JSON.toJSONString(res));
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Push message successfully for session[%s].", connectKey));
                        }
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
            listener.after(message, success, set.invalidDevices);
        }
        // TODO 根据set中的maybeCluster状态，添加中继到其他集群节点中进行转发
    }

    // 内部描述推送信息的类定义
    public class TarSessionSet {
        boolean maybeCluster = false;
        Set<String> invalidDevices = new HashSet<>();
        Set<Session> sessions = new HashSet<>();
    }
}

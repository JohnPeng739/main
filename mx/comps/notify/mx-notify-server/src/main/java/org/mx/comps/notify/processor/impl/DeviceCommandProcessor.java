package org.mx.comps.notify.processor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.StringUtils;
import org.mx.TypeUtils;
import org.mx.comps.notify.client.command.Command;
import org.mx.comps.notify.online.OnlineDevice;
import org.mx.comps.notify.processor.MessageProcessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.server.websocket.WsSessionManager;

import java.io.IOException;

/**
 * 终端命令处理器
 *
 * @author : john.peng created on date : 2018/1/5
 */
public abstract class DeviceCommandProcessor implements MessageProcessor {
    private static final Log logger = LogFactory.getLog(DeviceCommandProcessor.class);

    private String command;

    /**
     * 构造函数
     *
     * @param command 命令
     */
    public DeviceCommandProcessor(String command) {
        super();
        this.command = command;
    }

    /**
     * {@inheritDoc}
     *
     * @see MessageProcessor#getCommand()
     */
    @Override
    public String getCommand() {
        return command;
    }

    /**
     * 设备命令处理
     *
     * @param command      命令
     * @param type         类型
     * @param onlineDevice 设备对象
     * @return 成功处理返回true，否则返回false。
     */
    protected abstract boolean processCommand(String command, String type, OnlineDevice onlineDevice);

    /**
     * {@inheritDoc}
     *
     * @see MessageProcessor#processJsonCommand(Session, JSONObject)
     */
    @Override
    public boolean processJsonCommand(Session session, JSONObject json) {
        String command = json.getString("command");
        String type = json.getString("type");
        if (this.command.equals(command) && Command.CommandType.SYSTEM.name().equalsIgnoreCase(type)) {
            JSONObject data = json.getJSONObject("payload");
            if (data == null || StringUtils.isBlank(data.getString("deviceId"))) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("The payload is null for the command[%s].", command));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
            }
            // 构造在线设备对象
            String ip = TypeUtils.byteArray2Ip(session.getRemoteAddress().getAddress().getAddress());
            int port = session.getRemoteAddress().getPort();
            OnlineDevice device = new OnlineDevice();
            device.setDeviceId(data.getString("deviceId"));
            device.setState(data.getString("state"));
            device.setConnectKey(String.format("%s:%d", ip, port));
            device.setLastTime(data.getLongValue("lastTime"));
            device.setLastLongitude(data.getDoubleValue("lastLongitude"));
            device.setLastLatitude(data.getDoubleValue("lastLatitude"));
            device.setExtraData(data.getJSONObject("extraData"));
            return processCommand(command, type, device);
        } else {
            return false;
        }
    }

    /**
     * 向发出命令的终端回复一条响应消息
     *
     * @param connectKey 连接关键字
     * @param command    命令
     * @param deviceId   设备ID
     * @param error      错误信息
     */
    protected void sendResponseMessage(String connectKey, String command, String deviceId, String error) {
        WsSessionManager sessionManager = WsSessionManager.getManager();
        if (sessionManager != null) {
            Session session = sessionManager.getSession(connectKey);
            if (session != null) {
                JSONObject res = new JSONObject();
                res.put("srcCommand", command);
                res.put("deviceId", deviceId);
                boolean status = StringUtils.isBlank(error);
                res.put("status", status ? "ok" : "error");
                if (!status) {
                    res.put("error", error);
                }
                try {
                    // 向发送方回送一个推送结果消息
                    session.getRemote().sendString(JSON.toJSONString(res));
                } catch (IOException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error(String.format("Send registry response message to session[%s] fail.",
                                connectKey), ex);
                    }
                }
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The session[%s] not existed.", connectKey));
                }
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("The WsSessionManager is not initialized.");
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see MessageProcessor#processBinaryData(Session, byte[])
     */
    @Override
    public boolean processBinaryData(Session session, byte[] buffer) {
        return false;
    }
}

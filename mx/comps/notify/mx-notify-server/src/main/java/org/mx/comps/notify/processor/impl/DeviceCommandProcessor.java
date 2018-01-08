package org.mx.comps.notify.processor.impl;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.TypeUtils;
import org.mx.comps.notify.online.OnlineDevice;
import org.mx.comps.notify.processor.MessageProcessor;

import java.io.InputStream;

/**
 * 终端命令处理器
 *
 * @author : john.peng created on date : 2018/1/5
 */
public abstract class DeviceCommandProcessor implements MessageProcessor {
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
        JSONObject data = json.getJSONObject("data");
        String ip = TypeUtils.byteArray2Ipv4(session.getRemoteAddress().getAddress().getAddress());
        int port = session.getRemoteAddress().getPort();
        OnlineDevice device = new OnlineDevice();
        device.setDeviceId(data.getString("deviceId"));
        device.setState(data.getString("state"));
        device.setConnectKey(String.format("%s:%d", ip, port));
        device.setLastTime(data.getLongValue("lastTime"));
        device.setLastLongitude(data.getDoubleValue("lastLongitude"));
        device.setLastLatitude(data.getDoubleValue("lastLatitude"));
        return processCommand(command, type, device);
    }

    /**
     * {@inheritDoc}
     *
     * @see MessageProcessor#processBinaryData(Session, InputStream)
     */
    @Override
    public boolean processBinaryData(Session session, InputStream in) {
        return false;
    }
}

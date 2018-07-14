package org.mx.comps.notify.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.mx.comps.notify.client.command.NotifyCommand;
import org.mx.comps.notify.client.command.PingCommand;
import org.mx.comps.notify.client.command.RegistryCommand;
import org.mx.comps.notify.client.command.UnregistryCommand;
import org.mx.service.client.websocket.WebsocketClientListener;
import org.mx.service.client.websocket.WsClientInvoke;

/**
 * 基于Websocket接口调用的通知客户端
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class NotifyWsClient {
    private WsClientInvoke invoke = null;

    /**
     * 默认的构造函数
     */
    private NotifyWsClient() {
        super();
        this.invoke = new WsClientInvoke();
    }

    /**
     * 默认的构造函数，默认自动重连
     *
     * @param uri      连接的WebSocket URI
     * @param listener WebSocket客户端监听器
     */
    public NotifyWsClient(String uri, WebsocketClientListener listener) {
        this(uri, true, listener);
    }

    /**
     * 默认的构造函数
     *
     * @param uri       连接的WebSocket URI
     * @param reconnect 设置为true将自动重连
     * @param listener  WebSocket客户端监听器
     */
    public NotifyWsClient(String uri, boolean reconnect, WebsocketClientListener listener) {
        this();
        invoke = new WsClientInvoke();
        invoke.init(uri, listener, reconnect);
    }

    /**
     * 获取当前连接是否就绪
     *
     * @return 返回true表示已经就绪
     */
    public boolean isReady() {
        return invoke.isReady();
    }

    /**
     * 发送注册设备指令
     *
     * @param deviceId  设备标示ID
     * @param state     设备当前状态
     * @param longitude 设备当前经度
     * @param latitude  设备当前纬度
     * @param extraData 设备注册扩展信息
     */
    public void regiesty(String deviceId, String state, double longitude, double latitude, String extraData) {
        RegistryCommand command = new RegistryCommand(deviceId, state, longitude, latitude, extraData);
        invoke.send(JSON.toJSONString(command));
    }

    /**
     * 发送注销设备指令
     *
     * @param deviceId 设备标示ID
     */
    public void unregistry(String deviceId) {
        UnregistryCommand command = new UnregistryCommand(deviceId);
        invoke.send(JSON.toJSONString(command));
    }

    /**
     * 发送PING指令，终端状态报送指令
     *
     * @param deviceId  设备标示ID
     * @param state     设备当前状态
     * @param longitude 设备当前经度
     * @param latitude  设备当前纬度
     * @param extraData 报送终端状态的附加数据
     */
    public void ping(String deviceId, String state, double longitude, double latitude, JSONObject extraData) {
        PingCommand command = new PingCommand(deviceId, state, longitude, latitude, JSON.toJSONString(extraData));
        invoke.send(JSON.toJSONString(command));
    }

    /**
     * 发送推送通知指令
     *
     * @param src         推送源
     * @param deviceId    设备ID
     * @param tarType     目标类型
     * @param tar         推送目标
     * @param expiredTime 通知过期时间
     * @param notify      通知消息内容对象
     * @param <T>         通知消息内容对象泛型
     */
    public <T> void notify(String src, String deviceId, NotifyBean.TarType tarType, String tar, long expiredTime,
                           T notify) {
        NotifyCommand<T> command = new NotifyCommand<>(src, deviceId, tarType, tar, expiredTime, notify);
        invoke.send(JSON.toJSONString(command));
    }

    /**
     * 关闭，销毁资源
     */
    public void close() {
        if (invoke != null) {
            invoke.close();
        }
    }
}

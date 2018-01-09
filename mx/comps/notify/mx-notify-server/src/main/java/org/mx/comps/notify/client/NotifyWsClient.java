package org.mx.comps.notify.client;

import com.alibaba.fastjson.JSON;
import org.java_websocket.WebSocket;
import org.mx.comps.notify.client.command.NotifyCommand;
import org.mx.comps.notify.client.command.PongCommand;
import org.mx.comps.notify.client.command.RegistryCommand;
import org.mx.comps.notify.client.command.UnregistryCommand;
import org.mx.service.ws.client.BaseWebsocketClientListener;
import org.mx.service.ws.client.WsClientInvoke;

/**
 * 基于Websocket接口调用的通知客户端
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class NotifyWsClient {
    private WsClientInvoke invoke = null;
    private String uri = null;

    private NotifyWsClient() {
        super();
        this.invoke = new WsClientInvoke();
    }

    public NotifyWsClient(String uri, boolean reconnect, BaseWebsocketClientListener listener) {
        this();
        invoke = new WsClientInvoke();
        invoke.init(uri, listener, reconnect);
    }

    public WebSocket.READYSTATE getState() {
        return invoke.getState();
    }

    public void regiesty(String deviceId, String state, double longitude, double latitude) {
        RegistryCommand command = new RegistryCommand(deviceId, state, longitude, latitude);
        invoke.send(JSON.toJSONString(command));
    }

    public void unregistry(String deviceId) {
        UnregistryCommand command = new UnregistryCommand(deviceId);
        invoke.send(JSON.toJSONString(command));
    }

    public void pong(String deviceId, String state, double longitude, double latitude) {
        PongCommand command = new PongCommand(deviceId, state, longitude, latitude);
        invoke.send(JSON.toJSONString(command));
    }

    public <T> void notify(String src, NotifyBean.TarType tarType, String tar, long expiredTime,
                                              boolean needAck, T notify) {
        NotifyCommand<T> command = new NotifyCommand<T>(src, tarType, tar, expiredTime, needAck, notify);
        invoke.send(JSON.toJSONString(command));
    }

    public void close() {
        if (invoke != null) {
            invoke.close();
        }
    }
}

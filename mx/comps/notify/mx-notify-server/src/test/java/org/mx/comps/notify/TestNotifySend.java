package org.mx.comps.notify;

import com.alibaba.fastjson.JSONObject;
import org.java_websocket.WebSocket;
import org.junit.Test;
import org.mx.service.rest.client.RestClientInvoke;
import org.mx.service.ws.client.BaseWebsocketClientListener;
import org.mx.service.ws.client.WsClientInvoke;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TestNotifySend extends BaseTest {
    private JSONObject createRegistry() {
        JSONObject json = new JSONObject();
        json.put("command", "registry");
        json.put("type", "system");
        json.put("data", createDevice());
        return json;
    }

    private JSONObject createUnregistry() {
        JSONObject json = new JSONObject();
        json.put("command", "unregistry");
        json.put("type", "system");
        json.put("data", createDevice());
        return json;
    }

    private JSONObject createPong() {
        JSONObject json = new JSONObject();
        json.put("command", "pong");
        json.put("type", "system");
        json.put("data", createDevice());
        return json;
    }

    private JSONObject createNotify() {
        JSONObject json = new JSONObject();
        json.put("command", "notify");
        json.put("type", "user");
        json.put("data", createData());
        return json;
    }

    private JSONObject createDevice() {
        JSONObject json = new JSONObject();
        json.put("deviceId", "device01");
        json.put("state", "connect");
        json.put("lastTime", System.currentTimeMillis());
        json.put("lastLongitude", 129.1242);
        json.put("lastLatitude", 33.1241);
        return json;
    }

    private JSONObject createData() {
        JSONObject json = new JSONObject();
        json.put("src", "system");
        json.put("tarType", "ips");
        json.put("tar", "127.0.0.1");
        json.put("needAck", true);
        json.put("expiredTime", -1);
        json.put("message", createDevice());
        return json;
    }

    @Test
    public void test() {
        assertNotNull(context);
        WsClientInvoke wsClientInvoke = new WsClientInvoke();
        RestClientInvoke restClientInvoke = new RestClientInvoke();
        try {
            wsClientInvoke.init("ws://localhost:9997", new BaseWebsocketClientListener());
            Thread.sleep(500);
            assertEquals(WebSocket.READYSTATE.OPEN, wsClientInvoke.getState());
            wsClientInvoke.send(createRegistry().toJSONString());
            wsClientInvoke.send(createPong().toJSONString());
            wsClientInvoke.send(createNotify().toJSONString());
            restClientInvoke.post("http://localhost:9999/rest/notify/send", createData(), JSONObject.class);
            wsClientInvoke.send(createUnregistry().toJSONString());
        } catch (Exception ex) {
            fail(ex.getMessage());
        } finally {
            restClientInvoke.close();
            wsClientInvoke.close();
        }
    }
}

package org.mx.comps.notify;

import org.java_websocket.WebSocket;
import org.junit.Test;
import org.mx.comps.notify.client.NotifyBean;
import org.mx.comps.notify.client.NotifyRestClient;
import org.mx.comps.notify.client.NotifyWsClient;
import org.mx.comps.notify.test.TestNotifyEvent;
import org.mx.service.client.websocket.BaseWebsocketClientListener;

import static org.junit.Assert.*;

public class TestNotifySend extends BaseTest {
    @Test
    public void test() {
        assertNotNull(context);
        NotifyWsClient wsClient = new NotifyWsClient("ws://localhost:9997/notify", false, new BaseWebsocketClientListener());
        NotifyRestClient restClient = new NotifyRestClient("http://localhost:9999/rest/notify/send");
        try {
            Thread.sleep(500);
            assertEquals(WebSocket.READYSTATE.OPEN, wsClient.getState());
            wsClient.regiesty("device01", "registry", 129.1234, 31.1234);
            wsClient.pong("device01", "alive", 129.1234, 31.1234);
            TestNotifyEvent event = new TestNotifyEvent("id", "address", "description");
            wsClient.notify("system", NotifyBean.TarType.IPs, "127.0.0.1", -1, true, event);

            NotifyBean<TestNotifyEvent> notify = new NotifyBean<>("test system **", NotifyBean.TarType.IPs, "127.0.0.1",
                    -1, true, event);
            assertEquals(true, restClient.sendNotify(notify));
            wsClient.unregistry("device01");
        } catch (Exception ex) {
            fail(ex.getMessage());
        } finally {
            restClient.close();
            wsClient.close();
        }
    }
}

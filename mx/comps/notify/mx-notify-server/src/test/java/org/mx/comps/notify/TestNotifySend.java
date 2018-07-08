package org.mx.comps.notify;

import org.junit.Test;
import org.mx.comps.notify.client.NotifyBean;
import org.mx.comps.notify.client.NotifyRestClient;
import org.mx.comps.notify.client.NotifyWsClient;
import org.mx.comps.notify.test.TestNotifyEvent;
import org.mx.service.client.websocket.DefaultWebsocketClientMoniter;

import static org.junit.Assert.*;

public class TestNotifySend extends BaseTest {
    @Test
    public void test() {
        assertNotNull(context);
        NotifyWsClient wsClient = new NotifyWsClient("ws://localhost:9997/notify", false,
                new DefaultWebsocketClientMoniter());
        NotifyRestClient restClient = new NotifyRestClient("http://localhost:9999/rest/notify/send");
        try {
            Thread.sleep(500);
            assertTrue(wsClient.isReady());
            wsClient.regiesty("device02", "registry", 129.1234, 31.1234);
            wsClient.ping("device02", "alive", 129.1234, 31.1234);
            TestNotifyEvent event = new TestNotifyEvent("id", "address", "description");
            wsClient.notify("system", "device02",  NotifyBean.TarType.Devices, "device01",
                    -1, event);

            NotifyBean<TestNotifyEvent> notify = new NotifyBean<>("test system **", "device02",
                    NotifyBean.TarType.Devices, "device01",
                    -1, event);
            assertEquals(true, restClient.sendNotify(notify));
            wsClient.unregistry("device02");
        } catch (Exception ex) {
            fail(ex.getMessage());
        } finally {
            restClient.close();
            wsClient.close();
        }
    }
}

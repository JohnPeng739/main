package org.mx.comps.notify;

import org.junit.Test;
import org.mx.comps.notify.client.NotifyBean;
import org.mx.comps.notify.client.NotifyRestClient;
import org.mx.comps.notify.client.NotifyWsClient;
import org.mx.comps.notify.test.TestNotifyEvent;
import org.mx.service.client.websocket.DefaultWebsocketClientMoniter;

import static org.junit.Assert.*;

public class TestNotifySend {
    @Test
    public void test() {
        // assertNotNull(context);
        NotifyWsClient wsClient = new NotifyWsClient("ws://localhost:9997/notify", false,
                new DefaultWebsocketClientMoniter());
        NotifyRestClient restClient = new NotifyRestClient("http://localhost:9999/rest/notify/send");
        try {
            Thread.sleep(500);
            assertTrue(wsClient.isReady());
            wsClient.regiesty("device03", "registry", 129.1234, 31.1234,  null);
            wsClient.ping("device03", "alive", 129.1234, 31.1234);
            TestNotifyEvent event = new TestNotifyEvent("id", "address", "description");
            wsClient.notify("system", "device03",  NotifyBean.TarType.Devices, "device01",
                    -1, event);

            NotifyBean<TestNotifyEvent> notify = new NotifyBean<>("test system **", "device03",
                    NotifyBean.TarType.Devices, "device01,device02",
                    -1, event);
            assertEquals(true, restClient.sendNotify(notify));
            notify.setTarType(NotifyBean.TarType.States);
            notify.setTar("就绪");
            notify.setSrc("##########");
            restClient.sendNotify(notify);
            wsClient.unregistry("device03");
        } catch (Exception ex) {
            fail(ex.getMessage());
        } finally {
            restClient.close();
            wsClient.close();
        }
    }
}

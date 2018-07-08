package org.mx.service.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.client.websocket.DefaultWebsocketClientMoniter;
import org.mx.service.client.websocket.WsClientInvoke;
import org.mx.service.server.AbstractServerFactory;
import org.mx.service.server.WebsocketServerFactory;
import org.mx.service.test.config.TestDdosFilterConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.*;

/**
 * Created by john on 2017/11/4.
 */
public class TestWebsocketServerDdosFilter {
    private AnnotationConfigApplicationContext context = null;

    @Before
    public void before() {
        context = new AnnotationConfigApplicationContext(TestDdosFilterConfig.class);
    }

    @After
    public void after() {
        context.close();
    }

    @Test
    public void testNormalConnect() {
        AbstractServerFactory factory = context.getBean(WebsocketServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        try {
            ExecutorService service = Executors.newFixedThreadPool(50);
            Set<TestWebsocketTask> tasks = new HashSet<>();
            for (int index = 0; index < 19; index++) {
                tasks.add(new TestWebsocketTask(0, 5, false));
            }
            tasks.add(new TestWebsocketTask(1, 5, false));
            List<Future<Boolean>> result = service.invokeAll(tasks);
            boolean finish, success = true;
            do {
                finish = true;
                for (Future<Boolean> future : result) {
                    if (future.isDone()) {
                        try {
                            if (!future.get()) {
                                success = false;
                            }
                        } catch (ExecutionException ex) {
                            success = false;
                        }
                        break;
                    } else {
                        finish = false;
                    }
                }
            } while (!finish);
            if (!success) {
                fail("Some error");
            }
            service.shutdownNow();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testNumberFailConnect() {
        AbstractServerFactory factory = context.getBean(WebsocketServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        try {
            ExecutorService service = Executors.newFixedThreadPool(50);
            Set<TestWebsocketTask> tasks = new HashSet<>();
            for (int index = 0; index < 19; index++) {
                tasks.add(new TestWebsocketTask(0, 5, false));
            }
            tasks.add(new TestWebsocketTask(1, 5, false));
            tasks.add(new TestWebsocketTask(2, 5, true));
            tasks.add(new TestWebsocketTask(2, 5, true));
            List<Future<Boolean>> result = service.invokeAll(tasks);
            boolean finish, success = true;
            do {
                finish = true;
                for (Future<Boolean> future : result) {
                    if (future.isDone()) {
                        try {
                            future.get();
                        } catch (ExecutionException ex) {
                            ex.printStackTrace();
                            success = false;
                        }
                        break;
                    } else {
                        finish = false;
                    }
                }
            } while (!finish);
            if (!success) {
                fail("Some error");
            }
            Thread.sleep(3000);
            service.shutdownNow();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    private class TestWebsocketTask implements Callable<Boolean> {
        int connectDelaySec = 1, startDelaySec = 0;
        boolean needFail = false;

        public TestWebsocketTask() {
            super();
        }

        public TestWebsocketTask(int startDelaySec, int connectDelaySec, boolean needFail) {
            this();
            this.startDelaySec = startDelaySec;
            this.connectDelaySec = connectDelaySec;
            this.needFail = needFail;
        }

        @Override
        public Boolean call() throws Exception {
            if (startDelaySec > 0) {
                Thread.sleep(startDelaySec * 1000);
            }
            WsClientInvoke invoke1 = new WsClientInvoke();
            TestWebsocketMoniter listener = new TestWebsocketMoniter();
            invoke1.init(String.format("ws://localhost:%d/echo",
                    context.getEnvironment().getProperty("websocket.port", Integer.class, 9997)),
                    listener, false);
            Thread.sleep(1000 * connectDelaySec);
            boolean result = true;
            if (needFail) {
                try {
                    String msg = "hello, john";
                    invoke1.send(msg);
                    Thread.sleep(1000);
                    assertEquals(String.format("Server echo: %s.", msg), listener.textMsg);
                    result = false;
                } catch (Throwable ex) {
                    // ex.printStackTrace();
                }
            } else {
                try {
                    assertTrue(invoke1.isReady());
                    assertThat(listener.textMsg, startsWith("Server is ok:"));
                    String msg = "hello, john";
                    invoke1.send(msg);
                    Thread.sleep(1000);
                    assertEquals(String.format("Server echo: %s.", msg), listener.textMsg);
                    invoke1.close();
                    Thread.sleep(1000);
                    assertFalse(invoke1.isReady());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    result = false;
                }
            }
            invoke1.close();
            System.out.println("**********Test " + result + "*************");
            return result;
        }
    }

    private class TestWebsocketMoniter extends DefaultWebsocketClientMoniter {
        private String textMsg;
        private byte[] binaryMsg;

        @Override
        public void onBinaryMessage(Session session, byte[] bytes, int offset, int length) {
            this.binaryMsg = bytes;
        }

        @Override
        public void onTextMessage(Session session, String message) {
            this.textMsg = message;
        }
    }
}

package org.mx.service.server;

import org.eclipse.jetty.server.Server;
import org.java_websocket.WebSocket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mx.service.server.config.TestDdosFilterConfig;
import org.mx.service.ws.client.BaseWebsocketClientListener;
import org.mx.service.ws.client.WsClientInvoke;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * Created by john on 2017/11/4.
 */
public class TestDdosFilter {
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
                tasks.add(new TestWebsocketTask(0, 21, false));
            }
            List<Future<Boolean>> result = service.invokeAll(tasks);
            boolean finish, success = true;
            do {
                finish = true;
                for (Future<Boolean> future : result) {
                    if (future.isDone()) {
                        try {
                            future.get();
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
            for (int index = 0; index < 20; index++) {
                tasks.add(new TestWebsocketTask(0, 30, true));
            }
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
            service.shutdownNow();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testConfirmTimeoutConnect() {

        AbstractServerFactory factory = context.getBean(WebsocketServerFactory.class);
        assertNotNull(factory);
        Server server = factory.getServer();
        assertNotNull(server);

        try {
            ExecutorService service = Executors.newFixedThreadPool(50);
            Set<TestWebsocketTask> tasks = new HashSet<>();
            for (int index = 0; index < 10; index++) {
                tasks.add(new TestWebsocketTask(0, 20, true));
            }
            tasks.add(new TestWebsocketTask(12, 2, true));
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
            TestWebsocketListener listener = new TestWebsocketListener();
            invoke1.init("ws://localhost:9997/echo", listener);
            Thread.sleep(1000 * connectDelaySec);
            if (needFail) {
                try {
                    String msg = "hello, john";
                    invoke1.send(msg);
                    Thread.sleep(1000);
                    assertEquals(String.format("Server echo: %s.", msg), listener.textMsg);
                    fail("here need a exception.");
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    assertEquals(WebSocket.READYSTATE.OPEN, invoke1.getState());
                    assertEquals("Server is ok.", listener.textMsg);
                    String msg = "hello, john";
                    invoke1.send(msg);
                    Thread.sleep(1000);
                    assertEquals(String.format("Server echo: %s.", msg), listener.textMsg);
                    invoke1.close();
                    Thread.sleep(1000);
                    assertEquals(WebSocket.READYSTATE.CLOSED, invoke1.getState());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    fail(ex.getMessage());
                }
            }
            System.out.println("**********Test Successfully*************");
            return true;
        }
    }

    private class TestWebsocketListener extends BaseWebsocketClientListener {
        private String textMsg;
        private byte[] binaryMsg;

        @Override
        public void onOpen() {
            //
        }

        @Override
        public void onBinaryMessage(byte[] bytes) {
            this.binaryMsg = bytes;
        }

        @Override
        public void onTextMessage(String message) {
            this.textMsg = message;
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            //
        }

        @Override
        public void onError(Exception ex) {
            //
        }
    }
}
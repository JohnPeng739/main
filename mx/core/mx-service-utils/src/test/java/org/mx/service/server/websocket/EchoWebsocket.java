package org.mx.service.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component("echoWebsocket")
public class EchoWebsocket extends DefaultWsSessionMonitor {
    private static final Log logger = LogFactory.getLog(EchoWebsocket.class);
    private final CountDownLatch closeLatch;

    @Autowired
    private WsSessionManager manager = null;

    public EchoWebsocket() {
        super("/echo");
        closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return closeLatch.await(duration, unit);
    }

    @Override
    public void afterConnect(String connectKey) {
        super.afterConnect(connectKey);
        try {
            if (manager != null) {
                Session session = manager.getSession(connectKey);
                if (session != null) {
                    session.getRemote().sendString(String.format("Server is ok: %s.", connectKey));
                }
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
        }
    }

    @Override
    public void hasText(String connectKey, String message) {
        super.hasText(connectKey, message);
        try {
            if (manager != null) {
                Session session = manager.getSession(connectKey);
                if (session != null) {
                    session.getRemote().sendString(String.format("Server echo: %s.", message));
                }
            }
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
        }
    }

    @Override
    public void hasBinary(String connectKey, byte[] buffer) {
        super.hasBinary(connectKey, buffer);
        try {
            if (manager != null) {
                Session session = manager.getSession(connectKey);
                if (session != null) {
                    session.getRemote().sendBytes(ByteBuffer.wrap(buffer));
                }
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("some errors.", ex);
            }
        }
    }

    @Override
    public void afterClose(String connectKey, int code, String reason) {
        super.afterClose(connectKey, code, reason);
        closeLatch.countDown();
    }
}

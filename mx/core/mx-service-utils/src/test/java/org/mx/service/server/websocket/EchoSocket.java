package org.mx.service.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by john on 2017/11/4.
 */
@Component("echoSocket")
@WebSocket()
public class EchoSocket extends BaseWebsocket {
    private static final Log logger = LogFactory.getLog(EchoSocket.class);
    private final CountDownLatch closeLatch;

    public EchoSocket() {
        super("/upload");
        closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return closeLatch.await(duration, unit);
    }

    @Override
    public void onConnection(Session session) {
        super.onConnection(session);
        try {
            Future<Void> future;
            future = session.getRemote().sendStringByFuture("Hello");
            future.get(2, TimeUnit.SECONDS);
            future = session.getRemote().sendStringByFuture("Thanks for the conversation.");
            future.get(2, TimeUnit.SECONDS);
            session.close(StatusCode.NORMAL, "I'm done.");
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
        }
    }

    @Override
    public void onClose(Session session, int statusCode, String reason) {
        super.onClose(session, statusCode, reason);
        closeLatch.countDown();
    }
}

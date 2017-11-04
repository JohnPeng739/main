package org.mx.rest.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by john on 2017/11/4.
 */
@Component("echoSocket")
@WebSocket()
public class EchoSocket {
    private static final Log logger = LogFactory.getLog(EchoSocket.class);
    private final CountDownLatch closeLatch;

    public EchoSocket() {
        super();
        closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return closeLatch.await(duration, unit);
    }

    @OnWebSocketConnect
    public void onConnection(Session session) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got connection: %s%n", session));
        }
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

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Connection closed, status: %d, reason: %s.", statusCode, reason));
        }
        closeLatch.countDown();
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        if (logger.isErrorEnabled()) {
            logger.error("Got error.", throwable);
        }
    }

    @OnWebSocketFrame
    public void onFrame(Session session, Frame frame) {
        if (logger.isDebugEnabled()) {
            logger.debug("Got a frame.");
        }
    }

    @OnWebSocketMessage
    public void onBinaryMessage(Session session, InputStream in) {
        if (logger.isDebugEnabled()) {
            logger.debug("Got a binary message.");
        }
    }

    @OnWebSocketMessage
    public void onTextMessage(Session session, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Got a text message: %s.", message));
        }
    }
}

package org.mx.service.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.java_websocket.util.ByteBufferUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
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
        super("/echo");
        closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return closeLatch.await(duration, unit);
    }

    @Override
    public void afterConnect(Session session) {
        super.afterConnect(session);
        try {
            session.getRemote().sendString("Server is ok.");
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
        }
    }

    @Override
    public void receiveText(Session session, String message) {
        try {
            session.getRemote().sendString(String.format("Server echo: %s.", message));
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
        }
        super.receiveText(session, message);
    }

    @Override
    public void receiveBinary(Session session, InputStream in) {
        super.receiveBinary(session, in);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            long size = 0;
            do {
                int len = in.read(buffer);
                if (len > 0) {
                    out.write(buffer, 0, len);
                } else {
                    break;
                }
                size += len;
            } while (true);
            ByteBuffer bb = ByteBuffer.wrap(out.toByteArray());
            session.getRemote().sendBytes(bb);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("some errors.", ex);
            }
        }
        super.receiveBinary(session, in);
    }

    @Override
    public void beforeClose(Session session, int statusCode, String reason) {
        super.beforeClose(session, statusCode, reason);
        closeLatch.countDown();
    }
}

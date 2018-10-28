package org.mx.service.server.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

/**
 * 描述： 一个用于测试Websocket的ECHO
 *
 * @author john peng
 * Date time 2018/10/28 9:34 PM
 */
public class EchoWebsoket extends DefaultWsSessionMonitor {
    private static final Log logger = LogFactory.getLog(EchoWebsoket.class);

    private WsSessionManager manager;

    public EchoWebsoket() {
        super("/echo");
        manager = WsSessionManager.getManager();
    }

    @Override
    public void hasText(String connectKey, String text) {
        super.hasText(connectKey, text);
        try {
            if (manager != null) {
                Session session = manager.getSession(connectKey);
                if (session != null) {
                    session.getRemote().sendString(String.format("Server echo: %s.", text));
                }
            }
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Send any text fail.", ex);
            }
        }
    }
}

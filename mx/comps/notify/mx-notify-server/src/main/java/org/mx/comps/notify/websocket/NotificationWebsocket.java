package org.mx.comps.notify.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.mx.StringUtils;
import org.mx.comps.notify.processor.MessageProcessorChain;
import org.mx.service.server.websocket.BaseWebsocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 基于Websocket的通知通用消息服务器
 *
 * @author : john.peng created on date : 2018/1/3
 */
@Component("notifyWebsocket")
@WebSocket
public class NotificationWebsocket extends BaseWebsocket {
    private static final Log logger = LogFactory.getLog(NotificationWebsocket.class);

    @Autowired
    private MessageProcessorChain processorChain = null;

    public NotificationWebsocket() {
        super("/notify", false);
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#afterConnect(Session)
     */
    @Override
    protected void afterConnect(Session session) {
        super.afterConnect(session);
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#beforeClose(Session, int, String)
     */
    @Override
    protected void beforeClose(Session session, int statusCode, String reason) {
        super.beforeClose(session, statusCode, reason);
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#receiveText(Session, String)
     */
    @Override
    protected void receiveText(Session session, String message) {
        if (StringUtils.isBlank(message)) {
            if (logger.isWarnEnabled()) {
                logger.warn("The text message is blank.");
            }
        } else {
            try {
                JSONObject json = JSON.parseObject(message);
                processorChain.processJsonCommand(session, json);
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Parse message into JSONObject fail, message: %s.", message), ex);
                }
            }
        }
        super.receiveText(session, message);
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseWebsocket#receiveBinary(Session, InputStream)
     */
    @Override
    protected void receiveBinary(Session session, InputStream in) {
        processorChain.processBinaryData(session, in);
        super.receiveBinary(session, in);
    }
}

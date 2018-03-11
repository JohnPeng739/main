package org.mx.comps.notify.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.notify.processor.MessageProcessorChain;
import org.mx.service.server.websocket.DefaultWsSessionMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基于Websocket的通知通用消息服务
 *
 * @author : john.peng created on date : 2018/1/3
 */
@Component("notifyWebsocket")
public final class NotificationWebsocket extends DefaultWsSessionMonitor {
    private static final Log logger = LogFactory.getLog(NotificationWebsocket.class);

    @Autowired
    private MessageProcessorChain processorChain = null;

    public NotificationWebsocket() {
        super("/notify");
    }

    /**
     * {@inheritDoc}
     *
     * @see DefaultWsSessionMonitor#hasText(String, String)
     */
    @Override
    public void hasText(String connectKey, String message) {
        if (StringUtils.isBlank(message)) {
            if (logger.isWarnEnabled()) {
                logger.warn("The text message is blank.");
            }
        } else {
            try {
                JSONObject json = JSON.parseObject(message);
                processorChain.processJsonCommand(connectKey, json);
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Parse message into JSONObject fail, message: %s.", message), ex);
                }
            }
        }
        super.hasText(connectKey, message);
    }

    /**
     * {@inheritDoc}
     *
     * @see DefaultWsSessionMonitor#hasBinary(String, byte[]) (String, byte[])
     */
    @Override
    public void hasBinary(String connectKey, byte[] buffer) {
        processorChain.processBinaryData(connectKey, buffer);
        super.hasBinary(connectKey, buffer);
    }
}

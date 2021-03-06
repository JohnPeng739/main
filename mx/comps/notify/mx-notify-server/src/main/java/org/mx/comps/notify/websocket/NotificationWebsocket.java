package org.mx.comps.notify.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.notify.config.NotifyConfigBean;
import org.mx.comps.notify.processor.MessageProcessorChain;
import org.mx.service.server.websocket.WsLifeCircleMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基于Websocket的通知通用消息服务
 *
 * @author : john.peng created on date : 2018/1/3
 */
@Component("notifyWebsocket")
public final class NotificationWebsocket extends WsLifeCircleMonitor {
    private static final Log logger = LogFactory.getLog(NotificationWebsocket.class);

    private MessageProcessorChain processorChain;

    /**
     * 构造函数
     *
     * @param notifyConfigBean 推送配置对象
     * @param processorChain   消息处理链
     */
    @Autowired
    public NotificationWebsocket(NotifyConfigBean notifyConfigBean, MessageProcessorChain processorChain) {
        super(notifyConfigBean.getNotifyPath());
        this.processorChain = processorChain;
    }

    /**
     * {@inheritDoc}
     *
     * @see WsLifeCircleMonitor#hasText(String, String)
     */
    @Override
    public void hasText(String connectKey, String message) {
        super.hasText(connectKey, message);
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
    }

    /**
     * {@inheritDoc}
     *
     * @see WsLifeCircleMonitor#hasBinary(String, byte[]) (String, byte[])
     */
    @Override
    public void hasBinary(String connectKey, byte[] buffer) {
        super.hasBinary(connectKey, buffer);
        processorChain.processBinaryData(connectKey, buffer);
    }
}

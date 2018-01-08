package org.mx.comps.notify.processor.impl;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.comps.notify.processor.MessageProcessor;
import org.mx.comps.notify.processor.MessageProcessorChain;
import org.mx.comps.notify.processor.NotifyProcessor;
import org.mx.spring.SpringContextHolder;

import java.io.InputStream;

/**
 * 通知命令处理器
 *
 * @author : john.peng created on date : 2018/1/6
 */
public class NotifyCommandProcessor implements MessageProcessor {
    public static final String COMMAND = "notify";

    /**
     * {@inheritDoc}
     *
     * @see MessageProcessor#getCommand()
     */
    @Override
    public String getCommand() {
        return COMMAND;
    }

    /**
     * {@inheritDoc}
     *
     * @see MessageProcessor#processJsonCommand(Session, JSONObject)
     */
    @Override
    public boolean processJsonCommand(Session session, JSONObject json) {
        String command = json.getString("command");
        String type = json.getString("type");
        if (COMMAND.equals(command) && MessageProcessorChain.TYPE_USER.equals(type)) {
            // 通知消息
            JSONObject data = json.getJSONObject("data");
            NotifyProcessor notifyProcessor = SpringContextHolder.getBean(NotifyProcessor.class);
            notifyProcessor.notifyProcess(data);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see MessageProcessor#processBinaryData(Session, InputStream)
     */
    @Override
    public boolean processBinaryData(Session session, InputStream in) {
        return false;
    }
}

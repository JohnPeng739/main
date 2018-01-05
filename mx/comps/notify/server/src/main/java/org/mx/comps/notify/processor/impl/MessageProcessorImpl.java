package org.mx.comps.notify.processor.impl;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.websocket.api.Session;
import org.mx.comps.notify.processor.MessageProcesscor;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 通知消息服务处理器实现
 *
 * @author : john.peng created on date : 2018/1/3
 */
@Component("messageProcessor")
public class MessageProcessorImpl implements MessageProcesscor {
    @Override
    public void processJsonCommand(Session session, JSONObject json) {
        // TODO
    }

    @Override
    public void processBinaryData(Session session, InputStream in) {
        // TODO
    }
}

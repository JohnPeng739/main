package org.mx.comps.notify.processor;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.websocket.api.Session;

import java.io.InputStream;

/**
 * 消息处理接口定义
 *
 * @author : john.peng created on date : 2018/1/3
 */
public interface MessageProcesscor {
    void processJsonCommand(Session session, JSONObject json);

    void processBinaryData(Session session, InputStream in);
}

package org.mx.comps.notify.processor;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.websocket.api.Session;

import java.io.InputStream;

/**
 * 消息处理接口定义
 *
 * @author : john.peng created on date : 2018/1/3
 */
public interface MessageProcessor {
    /**
     * 返回命令
     *
     * @return 命令
     */
    String getCommand();

    /**
     * 处理一条JSON格式的消息
     *
     * @param session 会话
     * @param json    消息
     * @return 如果返回true，表示不需要再传递处理；否则返回false。
     */
    boolean processJsonCommand(Session session, JSONObject json);


    /**
     * 处理流式数据
     *
     * @param session 会话
     * @param in      输入流
     * @return 如果返回true，表示不需要再传递处理；否则返回false。
     */
    boolean processBinaryData(Session session, InputStream in);
}

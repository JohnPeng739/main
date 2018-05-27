package org.mx.service.server.comm;

/**
 * 描述： 接收到消息后的回调接口定义
 *
 * @author john peng
 * Date time 2018/5/27 下午3:26
 */
public interface ReceiverListener {
    /**
     * 接收到消息
     *
     * @param receivedMessage 接收到的消息对象
     */
    void receiveMessage(ReceivedMessage receivedMessage);
}

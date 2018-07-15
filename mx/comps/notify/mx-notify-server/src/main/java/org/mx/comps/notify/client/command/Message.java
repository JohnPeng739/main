package org.mx.comps.notify.client.command;

/**
 * 描述： 推送服务器中指令对象中的业务消息对象定义
 *
 * @author john peng
 * Date time 2018/7/14 上午11:00
 */
public class Message<T> {
    private String messageId, messageVersion;
    private T data;

    /**
     * 默认的构造函数
     */
    public Message() {
        super();
    }

    /**
     * 构造函数
     *
     * @param id      消息号
     * @param version 消息版本
     */
    public Message(String id, String version) {
        this();
        this.messageId = id;
        this.messageVersion = version;
    }

    /**
     * 构造函数
     *
     * @param id      消息号
     * @param version 消息版本
     * @param data    载荷数据
     */
    public Message(String id, String version, T data) {
        this(id, version);
        this.data = data;
    }

    /**
     * 获取消息号
     *
     * @return 消息号
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * 设置消息号
     *
     * @param messageId 消息号
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * 获取消息版本
     *
     * @return 消息版本
     */
    public String getMessageVersion() {
        return messageVersion;
    }

    /**
     * 设置消息版本
     *
     * @param messageVersion 消息版本
     */
    public void setMessageVersion(String messageVersion) {
        this.messageVersion = messageVersion;
    }

    /**
     * 获取消息对象中的载荷数据对象
     *
     * @return 载荷数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置消息对象中的载荷数据对象
     *
     * @param data 载荷数据
     */
    public void setData(T data) {
        this.data = data;
    }
}

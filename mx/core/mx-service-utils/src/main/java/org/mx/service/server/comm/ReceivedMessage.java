package org.mx.service.server.comm;

/**
 * 描述： 接收到的消息数据对象类定义
 *
 * @author john peng
 * Date time 2018/5/26 下午7:27
 */
public class ReceivedMessage {
    private String fromIp;
    private long offset = 0;
    private int fromPort, length;
    private byte[] data;

    /**
     * 获取数据来源IP
     *
     * @return IP
     */
    public String getFromIp() {
        return fromIp;
    }

    /**
     * 设置数据来源IP
     *
     * @param fromIp IP
     */
    public void setFromIp(String fromIp) {
        this.fromIp = fromIp;
    }

    /**
     * 获取数据偏移量
     *
     * @return 偏移量
     */
    public long getOffset() {
        return offset;
    }

    /**
     * 设置数据偏移量
     *
     * @param offset 偏移量
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    /**
     * 获取数据来源端口号
     *
     * @return 端口号
     */
    public int getFromPort() {
        return fromPort;
    }

    /**
     * 设置数据来源端口号
     *
     * @param fromPort 端口号
     */
    public void setFromPort(int fromPort) {
        this.fromPort = fromPort;
    }

    /**
     * 获取数据长度
     *
     * @return 长度
     */
    public int getLength() {
        return length;
    }

    /**
     * 设置数据长度
     *
     * @param length 长度
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * 获取接收到的数据
     *
     * @return 二进制数据
     */
    public byte[] getData() {
        return data;
    }

    /**
     * 设置接收到的数据
     *
     * @param data 二进制数据
     */
    public void setData(byte[] data) {
        this.data = data;
    }
}

package org.mx.service.server.comm;

/**
 * 描述：数据包封包和拆包 包装器
 *
 * @author john peng
 * Date time 2018/6/2 下午8:26
 */
public abstract class PacketWrapper {
    protected byte[] payload;

    /**
     * 获取发现的包装数据头偏移量
     *
     * @return 偏移量，如果没有发现包装数据帧，返回-1。
     */
    public abstract int getHeaderPosition();

    /**
     * 设置包装数据
     *
     * @param packetData 包装数据
     */
    public abstract void setPacketData(byte[] packetData);

    /**
     * 获取当前包装器中的有效载荷
     *
     * @return 有效数据载荷，如果没有发现有效载荷，返回null
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * 获取指定数据载荷的包装数据
     *
     * @param payload 有效数据载荷
     * @return 包装数据
     */
    public abstract byte[] packetPayload(byte[] payload);
}

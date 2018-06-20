package org.mx.service.server.comm;

/**
 * 描述： 一个没有任何包装的包装适配器
 *
 * @author john peng
 * Date time 2018/6/20 上午9:18
 */
public class SimplePacketWrapper extends PacketWrapper {
    /**
     * {@inheritDoc}
     *
     * @see PacketWrapper#getHeaderPosition()
     */
    @Override
    public int getHeaderPosition() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @see PacketWrapper#setPacketData(byte[])
     */
    @Override
    public void setPacketData(byte[] packetData) {
        super.payload = packetData;
    }

    /**
     * {@inheritDoc}
     *
     * @see PacketWrapper#packetPayload(byte[])
     */
    @Override
    public byte[] packetPayload(byte[] payload) {
        return payload;
    }
}

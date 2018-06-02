package org.mx.service.server.comm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.TypeUtils;

/**
 * 描述： 默认的数据包 包装器，提供同步字、数据长度(32位）、CRC32校验字（32位)。
 *
 * @author john peng
 * Date time 2018/6/2 下午8:37
 */
public class DefaultPacketWrapper extends PacketWrapper {
    private static final Log logger = LogFactory.getLog(DefaultPacketWrapper.class);

    private byte[] syncWords = {0x0, 0xf, 0x0, 0xf}, packetData = null, payload = null;
    private int headerPos = -1;

    /**
     * 查找同步字
     *
     * @return 存在同步字，则返回同步字的偏移量，否则返回-1。
     */
    private int findSyncWord() {
        if (packetData == null || syncWords == null || packetData.length == 0 || syncWords.length == 0) {
            return -1;
        }
        for (int i = 0; i < packetData.length; i++) {
            if (packetData[i] == syncWords[0]) {
                int j = 1;
                for (; j < syncWords.length; j++) {
                    if (packetData[i + j] != syncWords[j]) {
                        break;
                    }
                }
                if (j == syncWords.length) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     *
     * @see PacketWrapper#getHeaderPosition()
     */
    @Override
    public int getHeaderPosition() {
        return headerPos;
    }

    /**
     * {@inheritDoc}
     *
     * @see PacketWrapper#setPacketData(byte[])
     */
    @Override
    public void setPacketData(byte[] packetData) {
        this.headerPos = -1;
        this.payload = null;
        this.packetData = packetData;
        int found = findSyncWord();
        if (found == -1) {
            // 未发现同步字，直接返回
            return;
        }
        byte[] lengthArr = new byte[4], crcArr = new byte[4];
        int pos = found + syncWords.length;
        System.arraycopy(packetData, pos, lengthArr, 0, 4);
        int length = TypeUtils.byteArray2Int(lengthArr);
        if (found + length + 4 * 2 > packetData.length) {
            // 数据未完全容纳，直接返回
            return;
        }
        byte[] payload = new byte[length];
        pos += 4;
        System.arraycopy(packetData, pos, packetData, 0, length);
        pos += length;
        System.arraycopy(packetData, pos, crcArr, 0, 4);
        if (TypeUtils.byteArray2Int(crcArr) != DigestUtils.crc32(payload)) {
            // 数据校验不匹配，并需要对数据进行移位操作，避免下次再找到同样的同步字位置
            headerPos = found + 1;
            return;
        }
        // 条件全部满足，有效载荷返回
        headerPos = found + syncWords.length + 4;
        this.payload = payload;
    }

    /**
     * {@inheritDoc}
     *
     * @see PacketWrapper#getPayload()
     */
    @Override
    public byte[] getPayload() {
        return payload;
    }


    /**
     * {@inheritDoc}
     *
     * @see PacketWrapper#getPacketData(byte[])
     */
    @Override
    public byte[] getPacketData(byte[] payload) {
        if (payload == null) {
            return null;
        }
        int pos = 0;
        byte[] buffer = new byte[syncWords.length + payload.length + 4 * 2];
        System.arraycopy(syncWords, 0, buffer, pos, syncWords.length);
        pos += syncWords.length;
        System.arraycopy(TypeUtils.int2ByteArray(payload.length), 0, buffer, pos, 4);
        pos += 4;
        System.arraycopy(payload, 0, buffer, pos, payload.length);
        pos += payload.length;
        int crc = (int) DigestUtils.crc32(payload);
        System.arraycopy(TypeUtils.int2ByteArray(crc), 0, buffer, pos, 4);
        return buffer;
    }
}

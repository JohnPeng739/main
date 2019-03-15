package org.mx.service.server.comm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.TypeUtils;
import org.mx.service.error.UserInterfaceServiceErrorException;
import org.mx.service.server.CommServerConfigBean;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 描述： 基于UDP通信的服务提供者
 *
 * @author john peng
 * Date time 2018/5/26 下午7:16
 */
public class UdpCommServiceProvider extends CommServiceProvider {
    private static final Log logger = LogFactory.getLog(UdpCommServiceProvider.class);

    private DatagramSocket socket = null;
    private ExecutorService receiveExecutor = null;
    private ReceiveTask receiveTask = null;
    private PacketWrapper wrapper;

    /**
     * 默认的构造函数
     *
     * @param config    配置信息对象
     * @param wrapper 数据包装器
     */
    public UdpCommServiceProvider(CommServerConfigBean.UdpServerConfig config, PacketWrapper wrapper) {
        super(CommServiceType.UDP, config);
        this.wrapper = wrapper;
    }

    /**
     * {@inheritDoc}
     *
     * @see CommServiceProvider#init(ReceiverListener)
     */
    @Override
    public void init(ReceiverListener receiver) {
        try {
            CommServerConfigBean.UdpServerConfig config = (CommServerConfigBean.UdpServerConfig)super.config;
            socket = new DatagramSocket(config.getPort());
            if (config.getSoTimeout() > 0) {
                socket.setSoTimeout(config.getSoTimeout());
            }
            if (config.getSendBufferSize() >  0) {
                socket.setSendBufferSize(config.getSendBufferSize());
            }
            if (config.getReceiveBufferSize() >  0) {
                socket.setReceiveBufferSize(config.getReceiveBufferSize());
            }
            if (config.getTrafficClass() > 0) {
                socket.setTrafficClass(config.getTrafficClass());
            }
            socket.setReuseAddress(config.isReuseAddress());
            socket.setBroadcast(config.isBroadcast());
            // 启动接收线程
            receiveExecutor = Executors.newSingleThreadExecutor();
            final byte[] buffer = new byte[config.getMaxLength()];
            final DatagramPacket receivePacket = new DatagramPacket(buffer, config.getMaxLength());
            receiveTask = new ReceiveTask(socket, config.getMaxLength(), config.getPort(), receiver);
            receiveExecutor.submit(receiveTask);
        } catch (SocketException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Any socket exception, port: %d, timeout: %d, max length: %d.",
                        config.getPort(), config.getSoTimeout(), config.getMaxLength()), ex);
            }
            throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_SOCKET_ERROR);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see CommServiceProvider#close()
     */
    @Override
    public void close() {
        if (receiveTask != null) {
            receiveTask.close();
            receiveTask = null;
        }
        if (receiveExecutor != null) {
            try {
                receiveExecutor.awaitTermination(config.getSoTimeout(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Wait for receive task termination fail.", ex);
                }
            }
            receiveExecutor.shutdownNow();
            receiveExecutor = null;
        }
        if (socket != null) {
            socket.close();
            socket = null;
        }
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Close UDP Server successfully, port: %d.", config.getPort()));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see CommServiceProvider#send(String, int, byte[])
     */
    @Override
    public void send(String ip, int port, byte[] payload) {
        if (StringUtils.isBlank(ip) || port <= 0 || payload == null || payload.length <= 0) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Send parameter invalid, to: %s:%d, length: %d", ip, port,
                        payload == null ? 0 : payload.length));
            }
            return;
        }
        if (payload.length <= config.getMaxLength()) {
            if (wrapper != null) {
                // 如果设置了包装器，则对载荷进行包装
                payload = wrapper.packetPayload(payload);
            }
            try {
                SocketAddress socketAddress = new InetSocketAddress(ip, port);
                DatagramPacket sendPacket = new DatagramPacket(payload, payload.length, socketAddress);
                socket.send(sendPacket);
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Send data fail, to: %s:%d, length: %d.", ip, port, payload.length), ex);
                }
                throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_IO_ERROR);
            }
        } else {
            throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_DATA_TOO_MORE);
        }
    }

    /**
     * UDP消息接收任务
     */
    private class ReceiveTask implements Runnable {
        private DatagramSocket socket;
        private int length, localPort;
        private boolean needExit = false;
        private ReceiverListener receiver;

        /**
         * 默认的构造函数
         *
         * @param socket    UDP套接字
         * @param maxLength 缓存最大长度
         * @param localPort 本地监听端口号
         * @param receiver  接收到消息后的消费者
         */
        public ReceiveTask(DatagramSocket socket, int maxLength, int localPort, ReceiverListener receiver) {
            super();
            this.socket = socket;
            this.length = maxLength;
            this.localPort = localPort;
            this.receiver = receiver;
        }

        /**
         * 请求停止接收任务的运行
         */
        public void close() {
            needExit = true;
        }

        /**
         * {@inheritDoc}
         *
         * @see Runnable#run()
         */
        @Override
        public void run() {
            final byte[] buffer = new byte[length + wrapper.getExtraLength() + 10];
            final DatagramPacket packet = new DatagramPacket(buffer, length);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Start wait for receive UDP data, port: %d, buffer length: %d.",
                        socket.getLocalPort(), length));
            }
            while (!needExit) {
                try {
                    socket.receive(packet);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("***RECV***, length: %d, data length: %d.", packet.getLength(), packet.getData().length));
                    }
                     ReceivedMessage receivedMessage = new ReceivedMessage();
                    receivedMessage.setFromIp(TypeUtils.byteArray2Ip(packet.getAddress().getAddress()));
                    receivedMessage.setFromPort(packet.getPort());
                    receivedMessage.setLocalPort(localPort);
                    receivedMessage.setLength(packet.getLength() - packet.getOffset());
                    byte[] data = Arrays.copyOfRange(packet.getData(), packet.getOffset(),
                            packet.getOffset() + packet.getLength());
                    if (wrapper != null) {
                        // 如果设置了包装器，则对数据进行解包
                        wrapper.setPacketData(data);
                        receivedMessage.setPayload(wrapper.getPayload());
                    } else {
                        // 否则使用全部数据作为载荷
                        receivedMessage.setPayload(data);
                    }
                    if (receiver != null) {
                        receiver.receiveMessage(receivedMessage);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Receive a data packet, from: %s:%d, length: %d.",
                                receivedMessage.getFromIp(), receivedMessage.getFromPort(), receivedMessage.getLength()));
                    }
                    // 由于每次接收到数据后packet的长度将被实际长度填充，因此每次需要重置长度
                    packet.setLength(length + wrapper.getExtraLength() + 10);
                } catch (SocketTimeoutException ex) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Receive UDP data timeout.", ex);
                    }
                } catch (Exception ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error("Any IO exception.", ex);
                    }
                }
                try {
                    // 释放CPU时间
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Sleep interrupted.", ex);
                    }
                }
            }
            if (logger.isInfoEnabled()) {
                logger.info("The receive task finished, will be closed.");
            }
        }
    }
}

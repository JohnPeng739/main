package org.mx.service.server.comm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.TypeUtils;
import org.mx.service.error.UserInterfaceServiceErrorException;

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

    /**
     * 默认的构造函数
     *
     * @param port    监听端口号
     * @param length  缓存最大长度
     * @param timeout 等待超时值，单位为毫秒
     */
    public UdpCommServiceProvider(int port, int length, int timeout) {
        super(CommServiceType.UDP, port, length, timeout);
    }

    /**
     * {@inheritDoc}
     *
     * @see CommServiceProvider#init(ReceiverListener)
     */
    @Override
    public void init(ReceiverListener receiver) {
        try {
            socket = new DatagramSocket(super.port);
            socket.setSoTimeout(super.maxTimeout);
            socket.setReceiveBufferSize(super.maxLength);
            socket.setSendBufferSize(super.maxLength);
            // 启动接收线程
            receiveExecutor = Executors.newSingleThreadExecutor();
            final byte[] buffer = new byte[super.maxLength];
            final DatagramPacket receivePacket = new DatagramPacket(buffer, super.maxLength);
            receiveTask = new ReceiveTask(socket, super.maxLength, receiver);
            receiveExecutor.submit(receiveTask);
        } catch (SocketException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Any socket exception, port: %d, timeout: %d, max length: %d.",
                        super.port, super.maxTimeout, super.maxLength), ex);
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
                receiveExecutor.awaitTermination(super.maxTimeout, TimeUnit.MILLISECONDS);
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
            logger.info(String.format("Close UDP Server successfully, port: %d.", super.port));
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see CommServiceProvider#send(String, int, byte[])
     */
    @Override
    public void send(String ip, int port, byte[] buffer) {
        if (StringUtils.isBlank(ip) || port <= 0 || buffer == null || buffer.length <= 0) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Send parameter invalid, to: %s:%d, length: %d", ip, port,
                        buffer == null ? 0 : buffer.length));
            }
            return;
        }
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        try {
            for (int offset = 0; offset < buffer.length; ) {
                int length = Math.min(offset + maxLength, buffer.length);
                byte[] data = Arrays.copyOfRange(buffer, offset, length);
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, socketAddress);
                socket.send(sendPacket);
                if (offset + maxLength < buffer.length) {
                    offset += maxLength;
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Send data successfully, to: %s:%d, length: %d", ip, port, buffer.length));
            }
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Send data fail, to: %s:%d, length: %d.", ip, port, buffer.length), ex);
            }
        }
    }

    /**
     * UDP消息接收任务
     */
    private class ReceiveTask implements Runnable {
        private DatagramSocket socket;
        private int length;
        private boolean needExit = false;
        private ReceiverListener receiver;

        /**
         * 默认的构造函数
         *
         * @param socket    UDP套接字
         * @param maxLength 缓存最大长度
         * @param receiver  接收到消息后的消费者
         */
        public ReceiveTask(DatagramSocket socket, int maxLength, ReceiverListener receiver) {
            super();
            this.socket = socket;
            this.length = maxLength;
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
            final byte[] buffer = new byte[length];
            final DatagramPacket packet = new DatagramPacket(buffer, length);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Start wait for receive UDP data, port: %d, buffer length: %d.",
                        socket.getPort(), length));
            }
            try {
                while (!needExit) {
                    socket.receive(packet);
                    ReceivedMessage receivedMessage = new ReceivedMessage();
                    receivedMessage.setFromIp(TypeUtils.byteArray2Ip(packet.getAddress().getAddress()));
                    receivedMessage.setFromPort(packet.getPort());
                    receivedMessage.setLength(packet.getLength() - packet.getOffset());
                    receivedMessage.setData(Arrays.copyOfRange(packet.getData(), packet.getOffset(),
                            packet.getOffset() + packet.getLength()));
                    if (receiver != null) {
                        receiver.receiveMessage(receivedMessage);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Receive a data packet, from: %s:%d, length: %d.",
                                receivedMessage.getFromIp(), receivedMessage.getFromPort(), receivedMessage.getLength()));
                    }
                    // 由于每次接收到数据后packet的长度将被实际长度填充，因此每次需要重置长度
                    packet.setLength(length);
                }
                if (logger.isInfoEnabled()) {
                    logger.info("The receive task finished, will be closed.");
                }
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Any IO exception.", ex);
                }
                throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_IO_ERROR);
            }
        }
    }
}

package org.mx.service.server.comm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.TypeUtils;
import org.mx.service.error.UserInterfaceServiceErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * TCP连接类定义
 */
public class TcpConnection {
    private static final Log logger = LogFactory.getLog(TcpConnection.class);
    protected ExecutorService executorService = null;
    private PacketWrapper packetWrapper;
    private Socket socket;
    private ReceiverListener receiver;
    private int maxLength;
    private ReceiveTask receiveTask = null;

    /**
     * 默认的构造函数
     *
     * @param wrapper   数据包装器
     * @param socket    连接套接字
     * @param receiver  数据接收监听器
     * @param length    缓存最大长度
     * @param localPort 本地监听端口号
     */
    public TcpConnection(PacketWrapper wrapper, Socket socket, ReceiverListener receiver, int length, int localPort) {
        super();
        this.packetWrapper = wrapper;
        this.socket = socket;
        this.receiver = receiver;
        this.maxLength = length;
        try {
            String fromIp = TypeUtils.byteArray2Ip(socket.getInetAddress().getAddress());
            int fromPort = socket.getPort();
            executorService = Executors.newSingleThreadExecutor();
            executorService.submit(new ReceiveTask(fromIp, fromPort, socket.getInputStream(), maxLength, localPort));
            if (logger.isDebugEnabled()) {
                logger.debug("Initialize a new receive task successfully.");
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Any IO exception.", ex);
            }
        }
    }

    /**
     * 使用本TCP连接发送数据
     *
     * @param payload 待发送的数据
     */
    public void send(byte[] payload) {
        if (payload.length + packetWrapper.getExtraLength() <= maxLength) {
            if (packetWrapper != null) {
                // 如果设置了包装器，则对载荷进行包装
                payload = packetWrapper.packetPayload(payload);
            }
            try {
                socket.getOutputStream().write(payload);
                socket.getOutputStream().flush();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Send successfully, to: %s:%d, length: %d.",
                            TypeUtils.byteArray2Ip(socket.getInetAddress().getAddress()), socket.getPort(),
                            payload.length));
                }
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Send data fail, to: %s:%d, length: %d.",
                            TypeUtils.byteArray2Ip(socket.getInetAddress().getAddress()), socket.getPort(),
                            payload.length), ex);
                }
                throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_IO_ERROR);
            }
        } else {
            throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_DATA_TOO_MORE);
        }
    }

    /**
     * 关闭本TCP连接
     */
    public void close() {
        if (receiveTask != null) {
            receiveTask.close();
        }
        if (executorService != null) {
            try {
                executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Wait for any tasks termination fail.", ex);
                }
            }
            executorService.shutdownNow();
            executorService = null;
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Close socket fail.", ex);
                }
            }
        }
    }

    /**
     * 接收任务类定义
     */
    protected class ReceiveTask implements Runnable {
        private boolean needExit = false;
        private InputStream inputStream;
        private int length, fromPort, localPort;
        private String fromIp;

        /**
         * 默认的构造函数
         *
         * @param fromIp      来源IP
         * @param fromPort    来源端口号
         * @param inputStream 套接字的输入流
         * @param maxLength   缓存最大长度
         * @param localPort   本地监听端口号
         */
        public ReceiveTask(String fromIp, int fromPort, InputStream inputStream, int maxLength, int localPort) {
            super();
            this.fromIp = fromIp;
            this.fromPort = fromPort;
            this.inputStream = inputStream;
            this.length = maxLength;
            this.localPort = localPort;
        }

        /**
         * 请求停止本任务的运行
         */
        public void close() {
            this.needExit = true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            byte[] cache = new byte[length], buffer = new byte[length];
            int totalLength = 0, pos = 0;
            while (!needExit) {
                if (socket.isClosed()) {
                    break;
                }
                try {
                    int len = inputStream.read(buffer);
                    if (len > 0) {
                        if (len + totalLength > cache.length) {
                            System.arraycopy(cache, len, cache, 0, totalLength - len);
                            System.arraycopy(buffer, 0, cache, totalLength - len, len);
                        } else {
                            System.arraycopy(buffer, 0, cache, totalLength, len);
                            totalLength += len;
                        }

                        packetWrapper.setPacketData(cache);
                        byte[] payload = packetWrapper.getPayload();
                        if (payload != null) {
                            // 找到了有效载荷
                            ReceivedMessage receivedMessage = new ReceivedMessage();
                            receivedMessage.setFromIp(fromIp);
                            receivedMessage.setFromPort(fromPort);
                            receivedMessage.setLocalPort(localPort);
                            receivedMessage.setOffset(0);
                            receivedMessage.setLength(len);
                            receivedMessage.setPayload(payload);
                            if (receiver != null) {
                                receiver.receiveMessage(receivedMessage);
                            }
                            pos += packetWrapper.getHeaderPosition() + payload.length;
                            totalLength = totalLength - packetWrapper.getExtraLength() - packetWrapper.getPayload().length;
                            if (totalLength > 0) {
                                System.arraycopy(cache, pos, cache, 0, totalLength);
                            }
                            if (logger.isDebugEnabled()) {
                                logger.debug(String.format("Receive data, from: %s:%d, length: %d.", receivedMessage.getFromIp(),
                                        receivedMessage.getFromPort(), receivedMessage.getLength()));
                            }
                        } else {
                            // 没有找到载荷，但需要将找到的头数据忽略，重新移动缓存数据
                            pos = packetWrapper.getHeaderPosition();
                            if (pos > 0) {
                                totalLength -= pos;
                                System.arraycopy(cache, pos, cache, 0, totalLength);
                            }
                        }
                        pos = 0;
                    }
                } catch (SocketTimeoutException ex) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Receive timeout.", ex);
                    }
                } catch (IOException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error("Any IO exception.", ex);
                    }
                }
                try {
                    // 释放CPU时间
                    Thread.sleep(30);
                } catch (InterruptedException ex) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Receiver sleep interrupted.", ex);
                    }
                }
            }
        }
    }
}

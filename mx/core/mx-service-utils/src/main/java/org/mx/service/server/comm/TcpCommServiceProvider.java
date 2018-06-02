package org.mx.service.server.comm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.TypeUtils;
import org.mx.service.error.UserInterfaceServiceErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 描述： 基于TCP通信服务的提供者
 *
 * @author john peng
 * Date time 2018/5/26 下午7:17
 */
public class TcpCommServiceProvider extends CommServiceProvider {
    private static final Log logger = LogFactory.getLog(TcpCommServiceProvider.class);

    private ServerSocket serverSocket = null;
    private ExecutorService executorService = null;
    private SocketAcceptTask acceptTask = null;
    private Map<String, TcpConnection> tcpConnections;
    private ReceiverListener receiver = null;
    private PacketWrapper packetWrapper = null;

    /**
     * 默认的构造函数
     *
     * @param port    监听端口号
     * @param wrapper 数据包 包装器
     * @param length  缓存最大长度
     * @param timeout 等待超时值，单位为毫秒
     */
    public TcpCommServiceProvider(int port, PacketWrapper wrapper, int length, int timeout) {
        super(CommServiceType.TCP, port, length, timeout);
        this.packetWrapper = wrapper;
        tcpConnections = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     *
     * @see CommServiceProvider#init(ReceiverListener)
     */
    @Override
    public void init(ReceiverListener receiver) {
        this.receiver = receiver;
        try {
            serverSocket = new ServerSocket(super.port);
            serverSocket.setSoTimeout(super.maxTimeout);
            serverSocket.setReceiveBufferSize(super.maxLength);
            executorService = Executors.newCachedThreadPool();
            executorService.submit(new SocketAcceptTask(super.maxLength, super.maxTimeout));
            if (logger.isInfoEnabled()) {
                logger.info("Initialize the TCP server successfully.");
            }
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Any IO exception.", ex);
            }
            throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_IO_ERROR);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see CommServiceProvider#close()
     */
    @Override
    public void close() {
        if (acceptTask != null) {
            acceptTask.close();
            acceptTask = null;
        }
        if (tcpConnections != null && !tcpConnections.isEmpty()) {
            tcpConnections.forEach((k, v) -> v.close());
            tcpConnections.clear();
            tcpConnections = null;
        }
        if (executorService != null) {
            try {
                executorService.awaitTermination(super.maxTimeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Wait for any tasks termination fail.", ex);
                }
            }
            executorService.shutdownNow();
            executorService = null;
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Close server socket fail.", ex);
                }
                throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_IO_ERROR);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see CommServiceProvider#send(String, int, byte[])
     */
    @Override
    public void send(String ip, int port, byte[] payload) {
        String key = String.format("%s:%d", ip, port);
        if (tcpConnections.containsKey(key)) {
            tcpConnections.get(key).send(payload);
        } else {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The tcp connection[%s] not existed.", key));
            }
            throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_SOCKET_ERROR);
        }
    }

    /**
     * 服务器套接字等待连接任务类定义
     */
    private class SocketAcceptTask implements Runnable {
        private boolean needExit = false;
        private int length, timeout;

        /**
         * 默认的构造函数
         *
         * @param maxLength  缓存最大长度
         * @param maxTimeout 最大超时值
         */
        public SocketAcceptTask(int maxLength, int maxTimeout) {
            super();
            this.length = maxLength;
            this.timeout = maxTimeout;
        }

        /**
         * 请求停止服务器等待连接任务的运行
         */
        public void close() {
            needExit = true;
        }

        /**
         * {@inheritDoc}
         *
         * @see Runnable#run()
         */
        public void run() {
            try {
                while (!needExit) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Waiting for a new connection......");
                    }
                    Socket socket = serverSocket.accept();
                    String key = ipAndPort(socket.getInetAddress().getAddress(), socket.getPort());
                    tcpConnections.put(key, new TcpConnection(TypeUtils.byteArray2Ip(socket.getInetAddress().getAddress()),
                            socket.getPort(), socket, length, timeout));
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("There has a new connection: %s.", key));
                    }
                }
                if (logger.isInfoEnabled()) {
                    logger.info("Close the accept task successfully.");
                }
            } catch (SocketTimeoutException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("Timeout: %d ms.", timeout), ex);
                }
                throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_SOCKET_ERROR);
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Any IO exception.", ex);
                }
                throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_IO_ERROR);
            }
        }
    }

    /**
     * 接收任务类定义
     */
    private class ReceiveTask implements Runnable {
        private boolean needExit = false;
        private InputStream inputStream;
        private int length, fromPort;
        private String fromIp;

        /**
         * 默认的构造函数
         *
         * @param fromIp      来源IP
         * @param fromPort    来源端口号
         * @param inputStream 套接字的输入流
         * @param maxLength   缓存最大长度
         */
        public ReceiveTask(String fromIp, int fromPort, InputStream inputStream, int maxLength) {
            super();
            this.fromIp = fromIp;
            this.fromPort = fromPort;
            this.inputStream = inputStream;
            this.length = maxLength;
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
            try {
                byte[] cache = new byte[length], buffer = new byte[length];
                int totalLength = 0, pos = 0;
                while (!needExit) {
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
                                receivedMessage.setOffset(0);
                                receivedMessage.setLength(len);
                                receivedMessage.setPayload(payload);
                                if (receiver != null) {
                                    receiver.receiveMessage(receivedMessage);
                                }
                                pos += packetWrapper.getHeaderPosition() + payload.length;
                                totalLength = totalLength - pos;
                                System.arraycopy(cache, pos, cache, 0, totalLength);
                            } else {
                                // 没有找到载荷，但需要重新设置起点偏移
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
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Any IO exception.", ex);
                }
            }
        }
    }

    /**
     * TCP连接类定义
     */
    private class TcpConnection {
        private String fromIp;
        private Socket socket;
        private int maxLength, fromPort;
        private ReceiveTask receiveTask = null;

        /**
         * 默认的构造函数
         *
         * @param fromIp   来源IP
         * @param fromPort 来源端口
         * @param socket   连接套接字
         * @param length   缓存最大长度
         * @param timeout  最大超时值
         */
        public TcpConnection(String fromIp, int fromPort, Socket socket, int length, int timeout) {
            super();
            this.fromIp = fromIp;
            this.fromPort = fromPort;
            this.socket = socket;
            try {
                this.socket.setSoTimeout(timeout);
                this.maxLength = length;
                this.socket.setReceiveBufferSize(length);
                this.socket.setSendBufferSize(length);
            } catch (SocketException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Max length: %d, Timeout: %d ms.", length, timeout), ex);
                }
                throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_SOCKET_ERROR);
            }
            try {
                executorService.submit(new ReceiveTask(fromIp, fromPort, socket.getInputStream(), maxLength));
                if (logger.isDebugEnabled()) {
                    logger.debug("Initialize a new receive task successfully.");
                }
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Any IO exception.", ex);
                }
            }
        }

        /**
         * 使用本TCP连接发送数据
         *
         * @param buffer 待发送的数据
         */
        public void send(byte[] buffer) {
            try {
                for (int offset = 0; offset < buffer.length; ) {
                    byte[] payload = new byte[Math.min(offset + maxLength, buffer.length)];
                    System.arraycopy(buffer, offset, payload, 0, payload.length);
                    // 对载荷封包，然后发送
                    socket.getOutputStream().write(packetWrapper.getPacketData(payload));
                    if (offset + maxLength < buffer.length) {
                        offset += maxLength;
                    }
                }
            } catch (IOException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Any IO exception.", ex);
                }
                throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_IO_ERROR);
            }
        }

        /**
         * 关闭本TCP连接
         */
        public void close() {
            if (receiveTask != null) {
                receiveTask.close();
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
    }
}

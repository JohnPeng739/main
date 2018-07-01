package org.mx.service.server.comm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.service.error.UserInterfaceServiceErrorException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
    protected ExecutorService executorService = null;
    protected ReceiverListener receiver = null;
    private ServerSocket serverSocket = null;
    private SocketAcceptTask acceptTask = null;
    private Map<String, TcpConnection> tcpConnections;
    private PacketWrapper packetWrapper;

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
            if (super.maxTimeout > 0) {
                serverSocket.setSoTimeout(super.maxTimeout);
            }
            serverSocket.setReceiveBufferSize(super.maxLength);
            executorService = Executors.newSingleThreadExecutor();
            executorService.submit(new SocketAcceptTask(super.port, super.maxLength, super.maxTimeout));
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
        private int length, timeout, localPort;

        /**
         * 默认的构造函数
         *
         * @param localPort  本地监听端口号
         * @param maxLength  缓存最大长度
         * @param maxTimeout 最大超时值
         */
        public SocketAcceptTask(int localPort, int maxLength, int maxTimeout) {
            super();
            this.localPort = localPort;
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
            while (!needExit) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Waiting for a new connection......");
                }
                if (serverSocket.isClosed()) {
                    break;
                }
                try {
                    Socket socket = serverSocket.accept();
                    String key = ipAndPort(socket.getInetAddress().getAddress(), socket.getPort());
                    TcpConnection tcpConnection = new TcpConnection(packetWrapper, socket, receiver, length, timeout, localPort);
                    tcpConnections.put(key, tcpConnection);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("There has a new connection: %s.", key));
                    }
                } catch (SocketTimeoutException ex) {
                    if (logger.isWarnEnabled()) {
                        logger.warn(String.format("Timeout: %d ms.", timeout), ex);
                    }
                } catch (IOException ex) {
                    if (logger.isErrorEnabled()) {
                        logger.error("Any IO exception.", ex);
                    }
                }
            }
            if (logger.isInfoEnabled()) {
                logger.info("Close the accept task successfully.");
            }
        }
    }
}

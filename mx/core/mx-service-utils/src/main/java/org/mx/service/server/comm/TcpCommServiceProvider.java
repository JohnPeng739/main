package org.mx.service.server.comm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.service.error.UserInterfaceServiceErrorException;
import org.mx.service.server.CommServerConfigBean;

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
     * @param config  配置信息对象
     * @param wrapper 数据包 包装器
     */
    public TcpCommServiceProvider(CommServerConfigBean.TcpServerConfig config, PacketWrapper wrapper) {
        super(CommServiceType.TCP, config);
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
            CommServerConfigBean.TcpServerConfig config = (CommServerConfigBean.TcpServerConfig) super.config;
            serverSocket = new ServerSocket(config.getPort());
            if (config.getSoTimeout() > 0) {
                serverSocket.setSoTimeout(config.getSoTimeout());
            }
            executorService = Executors.newSingleThreadExecutor();
            executorService.submit(new SocketAcceptTask(config));
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
                executorService.awaitTermination(config.getSoTimeout(), TimeUnit.MILLISECONDS);
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
        private CommServerConfigBean.TcpServerConfig config;

        /**
         * 默认的构造函数
         *
         * @param config 配置信息对象
         */
        public SocketAcceptTask(CommServerConfigBean.TcpServerConfig config) {
            super();
            this.config = config;
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
                    if (config.getSoTimeout() > 0) {
                        socket.setSoTimeout(config.getSoTimeout());
                    }
                    if (config.getSendBufferSize() > 0) {
                        socket.setSendBufferSize(config.getSendBufferSize());
                    }
                    if (config.getReceiveBufferSize() > 0) {
                        socket.setReceiveBufferSize(config.getReceiveBufferSize());
                    }
                    socket.setTcpNoDelay(config.isNoDelay());
                    socket.setSoLinger(config.getSoLinger() != -1, config.getSoLinger());
                    socket.setOOBInline(config.isOobInline());
                    socket.setKeepAlive(config.isKeepAlive());
                    if (config.getTrafficClass() > 0) {
                        socket.setTrafficClass(config.getTrafficClass());
                    }
                    socket.setReuseAddress(config.isReuseAddress());
                    String key = ipAndPort(socket.getInetAddress().getAddress(), socket.getPort());
                    TcpConnection tcpConnection = new TcpConnection(packetWrapper, socket, receiver,
                            config.getMaxLength(), config.getPort());
                    tcpConnections.put(key, tcpConnection);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("There has a new connection: %s.", key));
                    }
                } catch (SocketTimeoutException ex) {
                    if (logger.isWarnEnabled()) {
                        logger.warn(String.format("Timeout: %d ms.", config.getSoTimeout()), ex);
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

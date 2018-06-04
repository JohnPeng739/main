package org.mx.service.client.comm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.service.error.UserInterfaceServiceErrorException;
import org.mx.service.server.comm.*;

import java.io.IOException;
import java.net.Socket;

/**
 * 描述： 基于TCP/UDP通信的客户端调用程序，支持发送和接收操作。
 *
 * @author john peng
 * Date time 2018/6/4 上午11:49
 */
public class CommClientInvoke {
    private static final Log logger = LogFactory.getLog(CommClientInvoke.class);

    private UdpCommServiceProvider udpProvider = null;
    private TcpConnection tcpConnection = null;

    private CommServiceProvider.CommServiceType type = CommServiceProvider.CommServiceType.UDP;
    private String ip;
    private int port;

    /**
     * 默认的构造函数
     *
     * @param type     类型，支持UDP和TCP两类通信
     * @param receiver 数据接收监听器
     * @param wrapper  数据包装器
     * @param ip       连接的IP地址
     * @param port     连接的端口号
     */
    public CommClientInvoke(CommServiceProvider.CommServiceType type, ReceiverListener receiver, PacketWrapper wrapper,
                            String ip, int port) {
        this(type, receiver, wrapper, ip, port, 8192, 1000);
    }

    /**
     * 默认的构造函数
     *
     * @param type     类型，支持UDP和TCP两类通信
     * @param receiver 数据接收监听器
     * @param wrapper  数据包装器
     * @param ip       连接的IP地址
     * @param port     连接的端口号
     * @param length   缓存长度
     * @param timeout  超时值
     */
    public CommClientInvoke(CommServiceProvider.CommServiceType type, ReceiverListener receiver, PacketWrapper wrapper,
                            String ip, int port, int length, int timeout) {
        super();
        initClient(type, receiver, wrapper, ip, port, length, timeout);
    }

    /**
     * 初始化连接客户端
     *
     * @param type     类型，支持UDP和TCP两类通信
     * @param receiver 数据接收监听器
     * @param wrapper  数据包装器
     * @param ip       连接的IP地址
     * @param port     连接的端口号
     * @param length   缓存长度
     * @param timeout  超时值
     */
    private void initClient(CommServiceProvider.CommServiceType type, ReceiverListener receiver, PacketWrapper wrapper,
                            String ip, int port, int length, int timeout) {
        this.type = type;
        this.ip = ip;
        this.port = port;
        if (type == CommServiceProvider.CommServiceType.UDP) {
            udpProvider = new UdpCommServiceProvider(port, wrapper, length, timeout);
            udpProvider.init(receiver);
        } else if (type == CommServiceProvider.CommServiceType.TCP) {
            tcpConnection = initTcpConnection(receiver, wrapper, length, timeout);
        }
    }

    /**
     * 初始化一个TCP连接客户端
     *
     * @param receiver 接收数据的监听器
     * @param wrapper  指定的数据包包装器
     * @param length   缓存大小
     * @param timeout  超时值
     * @return 创建成功后的TCP连接客户端
     */
    private TcpConnection initTcpConnection(ReceiverListener receiver, PacketWrapper wrapper, int length, int timeout) {
        try {
            Socket socket = new Socket(ip, port);
            return new TcpConnection(wrapper, socket, receiver, length, timeout);
        } catch (IOException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Create a socket[%s:%d] fail.", ip, port), ex);
            }
            throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_INITIALIZE_ERROR);
        }
    }

    /**
     * 清理并销毁通信客户端
     */
    public void close() {
        if (udpProvider != null) {
            udpProvider.close();
            udpProvider = null;
        }
        if (tcpConnection != null) {
            tcpConnection.close();
            tcpConnection = null;
        }
    }

    /**
     * 发送数据载荷
     *
     * @param payload 数据载荷
     */
    public void send(byte[] payload) {
        if (type == CommServiceProvider.CommServiceType.UDP) {
            if (udpProvider != null) {
                udpProvider.send(ip, port, payload);
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error("The UDP service provider not be initialized.");
                }
                throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_INITIALIZE_ERROR);
            }
        } else if (type == CommServiceProvider.CommServiceType.TCP) {
            if (tcpConnection != null) {
                tcpConnection.send(payload);
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error("The TCP service provider not be initialized.");
                }
                throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_INITIALIZE_ERROR);
            }
        } else {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Unsupported type: %s.", type));
            }
            throw new UserInterfaceServiceErrorException(UserInterfaceServiceErrorException.ServiceErrors.COMM_UNSUPPORTED_TYPE);
        }
    }
}

package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.server.comm.*;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述： 基于TCP/IP的通信服务器工厂，支持TCP和UDP通信。
 *
 * @author john peng
 * Date time 2018/5/26 下午7:11
 */
public class CommServerFactory {
    private static final Log logger = LogFactory.getLog(CommServerFactory.class);

    private CommServerConfigBean commServerConfigBean;
    private ApplicationContext context;
    private Map<Integer, TcpCommServiceProvider> tcpProviders;
    private Map<Integer, UdpCommServiceProvider> udpProviders;
    private boolean tcpEnabled = false, udpEnabled = false;

    /**
     * 默认的构造函数
     *
     * @param context              Spring IoC上下文
     * @param commServerConfigBean COMM通信配置对象
     */
    public CommServerFactory(ApplicationContext context, CommServerConfigBean commServerConfigBean) {
        super();
        this.commServerConfigBean = commServerConfigBean;
        this.context = context;
        this.tcpProviders = new HashMap<>();
        this.udpProviders = new HashMap<>();
    }

    private void initTcpProvider(CommServerConfigBean.TcpServerConfig serverConfig,
                                 Map<Integer, TcpCommServiceProvider> tcpProviders) {
        int port = serverConfig.getPort();
        String receiverName = serverConfig.getReceiver();
        String wrapperClassName = serverConfig.getPacketWrapper();
        if (port <= 0 || StringUtils.isBlank(receiverName)) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Invalid tcp server parameter, port: %d, receiver name: %s, wrapper class: %s.", port,
                        receiverName, wrapperClassName));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        try {
            PacketWrapper wrapper;
            if (StringUtils.isBlank(wrapperClassName)) {
                wrapper = new SimplePacketWrapper();
            } else {
                wrapper = (PacketWrapper) Class.forName(wrapperClassName).newInstance();
            }
            TcpCommServiceProvider tcpProvider = new TcpCommServiceProvider(serverConfig, wrapper);
            ReceiverListener receiver = context.getBean(receiverName, ReceiverListener.class);
            tcpProvider.init(receiver);
            tcpProviders.put(port, tcpProvider);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Create a tcp server successfully, port: %d, receiver: %s.", port, receiverName));
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Instantiate the PacketWrapper[%s] fail.", wrapperClassName), ex);
            }
        }
    }

    private void initUdpProvider(CommServerConfigBean.UdpServerConfig serverConfig,
                                 Map<Integer, UdpCommServiceProvider> udpProviders) {
        int port = serverConfig.getPort();
        String receiverName = serverConfig.getReceiver();
        String wrapperClassName = serverConfig.getPacketWrapper();
        if (port <= 0 || StringUtils.isBlank(receiverName)) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Invalid udp server parameter, port: %d, receiver name: %s.", port, receiverName));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        try {
            PacketWrapper wrapper;
            if (StringUtils.isBlank(wrapperClassName)) {
                wrapper = new SimplePacketWrapper();
            } else {
                wrapper = (PacketWrapper) Class.forName(wrapperClassName).newInstance();
            }
            UdpCommServiceProvider udpProvider = new UdpCommServiceProvider(serverConfig, wrapper);
            ReceiverListener receiver = context.getBean(receiverName, ReceiverListener.class);
            udpProvider.init(receiver);
            udpProviders.put(port, udpProvider);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Create a udp server successfully, port: %d, receiver: %s.", port, receiverName));
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Instantiate the PacketWrapper[%s] fail.", wrapperClassName), ex);
            }
        }
    }

    /**
     * 初始化通信服务器工厂
     */
    @SuppressWarnings("unchecked")
    public void init() {
        tcpEnabled = commServerConfigBean.isTcpEnabled();
        int tcpNum = commServerConfigBean.getTcpServerNum();
        if (tcpEnabled && tcpNum > 0) {
            for (CommServerConfigBean.ServerConfig serverConfig : commServerConfigBean.getTcpServers()) {
                initTcpProvider((CommServerConfigBean.TcpServerConfig) serverConfig, tcpProviders);
            }
        }

        udpEnabled = commServerConfigBean.isUdpEnabled();
        int udpNum = commServerConfigBean.getUdpServerNum();
        if (udpEnabled && udpNum > 0) {
            for (CommServerConfigBean.ServerConfig serverConfig : commServerConfigBean.getUdpServers()) {
                initUdpProvider((CommServerConfigBean.UdpServerConfig) serverConfig, udpProviders);
            }
        }

        if (!tcpEnabled && !udpEnabled && tcpNum <= 0 && udpNum <= 0) {
            if (logger.isWarnEnabled()) {
                logger.warn("There are not config the tcp && udp server.");
            }
        }
    }

    /**
     * 销毁通信服务器工厂
     */
    public void destroy() {
        if (tcpEnabled) {
            tcpProviders.forEach((k, v) -> v.close());
        }
        if (udpEnabled) {
            udpProviders.forEach((k, v) -> v.close());
        }
        if (tcpEnabled || udpEnabled) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Close the %d TCP providers and the %d UDP provider successfully.",
                        tcpProviders.size(), udpProviders.size()));
            }
        }
    }

    /**
     * 获取TCP通信服务提供者
     *
     * @param port 监听TCP的端口号
     * @return TCP服务提供者
     */
    public TcpCommServiceProvider getTcpProvider(int port) {
        return tcpProviders.get(port);
    }

    /**
     * 获取UDP通信服务提供者
     *
     * @param port 监听UDP的端口号
     * @return UDP服务提供者
     */
    public UdpCommServiceProvider getUdpProvider(int port) {
        return udpProviders.get(port);
    }
}

package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.server.comm.PacketWrapper;
import org.mx.service.server.comm.ReceiverListener;
import org.mx.service.server.comm.TcpCommServiceProvider;
import org.mx.service.server.comm.UdpCommServiceProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

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

    private Environment env;
    private ApplicationContext context;
    private Map<Integer, TcpCommServiceProvider> tcpProviders;
    private Map<Integer, UdpCommServiceProvider> udpProviders;

    /**
     * 默认的构造函数
     *
     * @param env     Spring IoC上下文环境
     * @param context Spring IoC上下文
     */
    public CommServerFactory(Environment env, ApplicationContext context) {
        super();
        this.env = env;
        this.context = context;
        this.tcpProviders = new HashMap<>();
        this.udpProviders = new HashMap<>();
    }

    private void initTcpProvider(int index, Map<Integer, TcpCommServiceProvider> tcpProviders) {
        int port = env.getProperty(String.format("tcp.servers.%d.port", index), Integer.class, 4000);
        int maxLength = env.getProperty(String.format("tcp.servers.%d.maxLength", index), Integer.class,
                8 * 1024);
        int maxTimeout = env.getProperty(String.format("tcp.servers.%d.maxTimeout", index), Integer.class,
                3000);
        String receiverName = env.getProperty(String.format("tcp.servers.%d.receiver", index));
        String wrapperClassName = env.getProperty(String.format("tcp.servers.%d.packet.wrapper", index));
        if (port <= 0 || maxLength <= 0 || maxTimeout <= 0 || StringUtils.isBlank(receiverName)
                || StringUtils.isBlank(wrapperClassName)) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Invalid tcp server parameter, port: %d, buffer length: %d, " +
                                "timeout: %d, receiver name: %s, wrapper class: %s.", port, maxLength, maxTimeout,
                        receiverName, wrapperClassName));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        try {
            PacketWrapper wrapper = (PacketWrapper) Class.forName(wrapperClassName).newInstance();
            TcpCommServiceProvider tcpProvider = new TcpCommServiceProvider(port, wrapper, maxLength, maxTimeout);
            ReceiverListener receiver = context.getBean(receiverName, ReceiverListener.class);
            tcpProvider.init(receiver);
            tcpProviders.put(port, tcpProvider);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Create a tcp server successfully, port: %d, buffer length: %d," +
                        "timeout: %d, receiver: %s.", port, maxLength, maxTimeout, receiverName));
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Instantiate the PacketWrapper[%s] fail.", wrapperClassName), ex);
            }
        }
    }

    private void initUdpProvider(int index, Map<Integer, UdpCommServiceProvider> udpProviders) {
        int port = env.getProperty(String.format("udp.servers.%d.port", index), Integer.class, 4000);
        int maxLength = env.getProperty(String.format("udp.servers.%d.maxLength", index), Integer.class, 8 * 1024);
        int maxTimeout = env.getProperty(String.format("udp.servers.%d.maxTimeout", index), Integer.class, 3000);
        String wrapperClassName = env.getProperty(String.format("udp.servers.%d.packet.wrapper", index));
        String receiverName = env.getProperty(String.format("udp.servers.%d.receiver", index));
        if (port <= 0 || maxLength <= 0 || maxTimeout <= 0 || StringUtils.isBlank(receiverName)
                || StringUtils.isBlank(wrapperClassName)) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Invalid udp server parameter, port: %d, buffer length: %d, " +
                                "timeout: %d, receiver name: %s, wrapper class: %s.", port, maxLength, maxTimeout,
                        receiverName, wrapperClassName));
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        try {
            PacketWrapper wrapper = (PacketWrapper) Class.forName(wrapperClassName).newInstance();
            UdpCommServiceProvider udpProvider = new UdpCommServiceProvider(port, wrapper, maxLength, maxTimeout);
            ReceiverListener receiver = context.getBean(receiverName, ReceiverListener.class);
            udpProvider.init(receiver);
            udpProviders.put(port, udpProvider);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Create a udp server successfully, port: %d, buffer length: %d," +
                        "timeout: %d, receiver: %s.", port, maxLength, maxTimeout, receiverName));
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
        boolean tcpEnabled = env.getProperty("tcp.enabled", Boolean.class, false);
        int tcpNum = env.getProperty("tcp.servers.num", Integer.class, 0);
        if (tcpEnabled && tcpNum > 0) {
            for (int index = 1; index <= tcpNum; index++) {
                initTcpProvider(index, tcpProviders);
            }
        }

        boolean udpEnabled = env.getProperty("udp.enabled", Boolean.class, false);
        int udpNum = env.getProperty("udp.servers.num", Integer.class, 0);
        if (udpEnabled && udpNum > 0) {
            for (int index = 1; index <= udpNum; index++) {
                initUdpProvider(index, udpProviders);
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
        tcpProviders.forEach((k, v) -> v.close());
        udpProviders.forEach((k, v) -> v.close());
        if (logger.isInfoEnabled()) {
            logger.info(String.format("Close the %d TCP providers and the %d UDP provider successfully.",
                    tcpProviders.size(), udpProviders.size()));
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

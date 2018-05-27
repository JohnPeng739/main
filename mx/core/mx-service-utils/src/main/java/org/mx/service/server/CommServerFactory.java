package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.server.comm.CommServiceProvider;
import org.mx.service.server.comm.ReceiverListener;
import org.mx.service.server.comm.TcpCommServiceProvider;
import org.mx.service.server.comm.UdpCommServiceProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

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
    private CommServiceProvider tcpProvider, udpProvider;

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
    }

    /**
     * 初始化通信服务器工厂
     */
    public void init() {
        boolean tcpEnabled = env.getProperty("tcp.enabled", Boolean.class, false);
        if (tcpEnabled) {
            int port = env.getProperty("tcp.port", Integer.class, 4000);
            int maxLength = env.getProperty("tcp.maxLength", Integer.class, 8 * 1024);
            int maxTimeout = env.getProperty("tcp.maxTimeout", Integer.class, 3000);
            String receiverName = env.getProperty("tcp.receiver");
            if (port <= 0 || maxLength <= 0 || maxTimeout <= 0 || StringUtils.isBlank(receiverName)) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Invalid tcp server parameter, port: %d, buffer length: %d, " +
                            "timeout: %d, receiver name: %s.", port, maxLength, maxTimeout, receiverName));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
            }
            tcpProvider = new TcpCommServiceProvider(port, maxLength, maxTimeout);
            ReceiverListener receiver = context.getBean(receiverName, ReceiverListener.class);
            tcpProvider.init(receiver);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Create a tcp server successfully, port: %d, buffer length: %d," +
                        "timeout: %d, receiver: %s.", port, maxLength, maxTimeout, receiverName));
            }
        }

        boolean udpEnabled = env.getProperty("udp.enabled", Boolean.class, false);
        if (udpEnabled) {
            int port = env.getProperty("udp.port", Integer.class, 4000);
            int maxLength = env.getProperty("udp.maxLength", Integer.class, 8 * 1024);
            int maxTimeout = env.getProperty("udp.maxTimeout", Integer.class, 3000);
            String receiverName = env.getProperty("udp.receiver");
            if (port <= 0 || maxLength <= 0 || maxTimeout <= 0 || StringUtils.isBlank(receiverName)) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Invalid udp server parameter, port: %d, buffer length: %d, " +
                            "timeout: %d, receiver name: %s.", port, maxLength, maxTimeout, receiverName));
                }
                throw new UserInterfaceSystemErrorException(
                        UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
            }
            udpProvider = new UdpCommServiceProvider(port, maxLength, maxTimeout);
            ReceiverListener receiver = context.getBean(receiverName, ReceiverListener.class);
            udpProvider.init(receiver);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Create a udp server successfully, port: %d, buffer length: %d," +
                        "timeout: %d, receiver: %s.", port, maxLength, maxTimeout, receiverName));
            }
        }

        if (!tcpEnabled && !udpEnabled) {
            if (logger.isWarnEnabled()) {
                logger.warn("There are not config the tcp && udp server.");
            }
        }
    }

    /**
     * 销毁通信服务器工厂
     */
    public void destroy() {
        if (tcpProvider != null) {
            tcpProvider.close();
            tcpProvider = null;
        }
        if (udpProvider != null) {
            udpProvider.close();
            udpProvider = null;
        }
    }

    /**
     * 获取TCP通信服务提供者
     *
     * @return TCP服务提供者
     */
    public CommServiceProvider getTcpProvider() {
        return tcpProvider;
    }

    /**
     * 获取UDP通信服务提供者
     *
     * @return UDP服务提供者
     */
    public CommServiceProvider getUdpProvider() {
        return udpProvider;
    }
}
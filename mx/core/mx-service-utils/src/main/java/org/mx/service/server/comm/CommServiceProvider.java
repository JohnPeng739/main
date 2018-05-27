package org.mx.service.server.comm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.TypeUtils;
import org.mx.service.error.UserInterfaceServiceErrorException;

import java.io.UnsupportedEncodingException;

/**
 * 描述： 抽象的通信服务提供者，TCP和UDP的通信服务提供者均继承本类。
 *
 * @author john peng
 * Date time 2018/5/26 下午7:14
 */
public abstract class CommServiceProvider {
    private static final Log logger = LogFactory.getLog(CommServiceProvider.class);

    protected CommServiceType type;
    protected int port;
    protected int maxLength, maxTimeout;

    /**
     * 默认的构造函数
     *
     * @param type       通信类型，TCP | UDP
     * @param port       监听端口号
     * @param maxLength  缓存最大长度
     * @param maxTimeout 等待超时值，单位为毫秒
     */
    public CommServiceProvider(CommServiceType type, int port, int maxLength, int maxTimeout) {
        super();
        this.type = type;
        this.port = port;
        this.maxLength = maxLength;
        this.maxTimeout = maxTimeout;
    }

    /**
     * 格式化IP地址和端口号为标准格式
     *
     * @param ip   IP地址，IPv4或IPv6
     * @param port 端口号
     * @return 格式化后的格式
     */
    protected String ipAndPort(byte[] ip, int port) {
        return String.format("%s:%d", TypeUtils.byteArray2Ip(ip), port);
    }

    /**
     * 初始化通信服务提供者
     *
     * @param receiver 消息接收监听器
     */
    public abstract void init(ReceiverListener receiver);

    /**
     * 关闭通信服务提供者
     */
    public abstract void close();

    /**
     * 向指定目标发送文本数据消息，默认使用"utf-8"字符集
     *
     * @param ip      目标IP，IPv4或IPv6
     * @param port    端口号
     * @param message 文本消息
     */
    public void send(String ip, int port, String message) {
        this.send(ip, port, message, "utf-8");
    }

    /**
     * 向指定目标发送文本数据消息
     *
     * @param ip          目标IP，IPv4或IPv6
     * @param port        端口号
     * @param message     文本消息
     * @param charsetName 文本消息字符集
     */
    public void send(String ip, int port, String message, String charsetName) {
        try {
            this.send(ip, port, message.getBytes(charsetName));
        } catch (UnsupportedEncodingException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Unsupported encoding: %s", charsetName), ex);
            }
            throw new UserInterfaceServiceErrorException(
                    UserInterfaceServiceErrorException.ServiceErrors.COMM_UNSUPPORTED_ENCODING);
        }
    }

    /**
     * 向指定目标发送二进制数据消息
     *
     * @param ip     目标IP，IPv4或IPv6
     * @param port   端口号
     * @param buffer 二进制数据
     */
    public abstract void send(String ip, int port, byte[] buffer);

    /**
     * 通信服务类型
     */
    public enum CommServiceType {
        TCP, UDP
    }
}

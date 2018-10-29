package org.mx.service.server;

/**
 * 描述： 抽象的HTTP服务器配置对象
 *
 * @author john peng
 * Date time 2018/7/18 下午1:16
 */
public abstract class HttpServerConfigBean {
    private ServerType serverType = ServerType.RESTful;

    /**
     * 构造函数
     *
     * @param serverType 服务器类型
     * @see ServerType
     */
    public HttpServerConfigBean(ServerType serverType) {
        super();
        this.serverType = serverType;
    }

    /**
     * 获取服务器类型
     *
     * @return 服务器类型
     */
    public ServerType getServerType() {
        return serverType;
    }

    /**
     * 获取是否启用
     *
     * @return 返回true表示启用，否则不启用
     */
    public abstract boolean isEnabled();

    /**
     * 获取监听端口号
     *
     * @return 端口号
     */
    public abstract int getPort();

    /**
     * 获取线程数量
     *
     * @return 线程数量
     */
    public abstract int getThreads();

    /**
     * 获取是否使用SSL
     *
     * @return 返回true表示使用SSL加密，否则不使用
     */
    public abstract boolean isSecurity();

    /**
     * 获取Keystore路径
     *
     * @return Keystore路径
     */
    public abstract String getKeystorePath();

    /**
     * 获取Keystore密码
     *
     * @return Keystore密码
     */
    public abstract String getKeystorePassword();

    /**
     * 获取Keystore管理员密码
     *
     * @return Keystore管理员密码
     */
    public abstract String getKeyManagerPassword();

    /**
     * 获取Keystore类型，默认为JKS
     *
     * @return Keystore类型
     */
    public abstract String getKeystoreType();

    /**
     * 获取闲置超时值
     *
     * @return 闲置超时值
     */
    public abstract long getIdleTimeoutSecs();

    /**
     * 获取输出缓冲区大小
     *
     * @return 缓冲区大小
     */
    public abstract long getOutputSize();

    /**
     * 获取请求头大小
     *
     * @return 请求头大小
     */
    public abstract long getRequestHeaderSize();

    /**
     * 获取响应头大小
     *
     * @return 响应头大小
     */
    public abstract long getResponseHeaderSize();

    /**
     * 服务器类型，支持：RESTful、Servlet、WebScoket服务器。
     */
    public enum ServerType {
        RESTful, Servlet, WebSocket
    }
}

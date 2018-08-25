package org.mx.service.server;

import org.mx.StringUtils;
import org.mx.TypeUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * 描述： RESTful服务配置对象
 *
 * @author john peng
 * Date time 2018/7/18 下午1:17
 */
public class ServletServerConfigBean extends HttpServerConfigBean {
    @Value("${servlet.enabled:false}")
    private boolean enabled;
    @Value("${servlet.port:9999}")
    private int port;
    @Value("${servlet.threads:100}")
    private int threads;
    @Value("${servlet.security:false}")
    private boolean security;
    @Value("${servlet.security.keystore:}")
    private String keystorePath;
    @Value("${servlet.security.keystorePassword:}")
    private String keystorePassword;
    @Value("${servlet.security.keyManagerPassword:}")
    private String keyManagerPassword;

    @Value("${servlet.idleTimeoutSecs:300}")
    private int idleTimeoutSecs;
    @Value("${servlet.outputSize:32K}")
    private String outputSizeString;
    @Value("${servlet.requestHeaderSize:8K}")
    private String requestHeaderSizeString;
    @Value("${servlet.responseHeaderSize:8K}")
    private String responseHeaderSizeString;

    @Value("${servlet.service.classes:}")
    private String serviceClasses;

    /**
     * 默认的构造函数
     */
    public ServletServerConfigBean() {
        super(ServerType.Servlet);
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#isEnabled()
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getPort()
     */
    public int getPort() {
        return port;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getThreads()
     */
    public int getThreads() {
        return threads;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#isSecurity()
     */
    public boolean isSecurity() {
        return security;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getKeystorePath()
     */
    public String getKeystorePath() {
        return keystorePath;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getKeystorePassword()
     */
    public String getKeystorePassword() {
        return keystorePassword;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getKeyManagerPassword()
     */
    public String getKeyManagerPassword() {
        return keyManagerPassword;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getIdleTimeoutSecs()
     */
    public long getIdleTimeoutSecs() {
        return idleTimeoutSecs;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getOutputSize()
     */
    public long getOutputSize() {
        return TypeUtils.string2Size(outputSizeString, 32 * 1024);
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getRequestHeaderSize()
     */
    public long getRequestHeaderSize() {
        return TypeUtils.string2Size(requestHeaderSizeString, 8 * 1024);
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getResponseHeaderSize()
     */
    public long getResponseHeaderSize() {
        return TypeUtils.string2Size(responseHeaderSizeString, 8 * 1024);
    }

    /**
     * 获取配置的Servlet服务类列表
     *
     * @return Servlet服务类类别
     */
    public String[] getServiceClasses() {
        return StringUtils.split(serviceClasses);
    }
}

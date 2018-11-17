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
public class RestfulServerConfigBean extends HttpServerConfigBean {
    @Value("${restful.enabled:false}")
    private boolean enabled;
    @Value("${restful.port:9999}")
    private int port;
    @Value("${restful.threads:100}")
    private int threads;
    @Value("${restful.security:false}")
    private boolean security;
    @Value("${restful.security.keystore:}")
    private String keystorePath;
    @Value("${restful.security.keystorePassword:}")
    private String keystorePassword;
    @Value("${restful.security.keyManagerPassword:}")
    private String keyManagerPassword;
    @Value("${restful.security.keystoreType:JKS}")
    private String keystoreType;
    @Value("${restful.security.keyAlias:jetty}")
    private String keyAlias;

    @Value("${restful.idleTimeoutSecs:300}")
    private int idleTimeoutSecs;
    @Value("${restful.outputSize:32K}")
    private String outputSizeString;
    @Value("${restful.requestHeaderSize:8K}")
    private String requestHeaderSizeString;
    @Value("${restful.responseHeaderSize:8K}")
    private String responseHeaderSizeString;

    @Value("${restful.service.classes:}")
    private String serviceClasses;
    @Value("${restful.service.base.packages:}")
    private String resourceBasePackages;

    /**
     * 默认的构造函数
     */
    public RestfulServerConfigBean() {
        super(ServerType.RESTful);
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
     * @see HttpServerConfigBean#getKeystoreType()
     */
    public String getKeystoreType() {
        return keystoreType;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerConfigBean#getKeyAlias()
     */
    public String getKeyAlias() {
        return keyAlias;
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
     * 获取配置的RESTful服务类列表
     *
     * @return RESTful服务类类别
     */
    public String[] getServiceClasses() {
        return StringUtils.isBlank(serviceClasses) ? null : StringUtils.split(serviceClasses);
    }

    /**
     * 获取配置的RESTful服务资源基准包路径，如果有多个包，用逗号分隔。系统将自动扫描。
     *
     * @return RESTful服务资源基本包路径
     */
    public String[] getResourceBasePackages() {
        String packages = StringUtils.isBlank(resourceBasePackages) ? super.getSpringBootClassPackage() : resourceBasePackages;
        return StringUtils.split(packages);
    }
}

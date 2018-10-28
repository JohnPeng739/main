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
public class WebsocketServerConfigBean extends HttpServerConfigBean {
    @Value("${websocket.enabled:false}")
    private boolean enabled;
    @Value("${websocket.port:9999}")
    private int port;
    @Value("${websocket.echo.enabled:true}")
    private boolean echoEnabled;
    @Value("${websocket.threads:100}")
    private int threads;
    @Value("${websocket.security:false}")
    private boolean security;
    @Value("${websocket.security.keystore:}")
    private String keystorePath;
    @Value("${websocket.security.keystorePassword:}")
    private String keystorePassword;
    @Value("${websocket.security.keyManagerPassword:}")
    private String keyManagerPassword;

    @Value("${websocket.idleTimeoutSecs:300}")
    private int idleTimeoutSecs;
    @Value("${websocket.outputSize:32K}")
    private String outputSizeString;
    @Value("${websocket.requestHeaderSize:8K}")
    private String requestHeaderSizeString;
    @Value("${websocket.responseHeaderSize:8K}")
    private String responseHeaderSizeString;

    @Value("${websocket.session.ping.cycleSec:5}")
    private int pingCycleSec;
    @Value("${websocket.session.clean.cycleSec:30}")
    private int cleanCycleSec;
    @Value("${websocket.session.filter.rules:}")
    private String filterRulesString;
    @Value("${websocket.session.filter.rules.list.allows:}")
    private String filterRulesAllows;
    @Value("${websocket.session.filter.rules.list.blocks:}")
    private String filterRulesBlocks;
    @Value("${websocket.session.filter.rules.ddos.cycleSec:30}")
    private int filterRulesDdosCycleSec;
    @Value("${websocket.session.filter.rules.ddos.maxConnections:20}")
    private int filterRulesDdosMaxConnections;
    @Value("${websocket.asyncWriteTimeoutSecs:60}")
    private int asyncWriteTimeoutSecs;
    @Value("${websocket.inputBufferSize:4K}")
    private String inputBufferSizeString;
    @Value("${websocket.maxTextMessageSize:64K}")
    private String maxTextMessageSizeString;
    @Value("${websocket.maxTextMessageBufferSize:32K}")
    private String maxTextMessageBufferSizeString;
    @Value("${websocket.maxBinaryMessageSize:64K}")
    private String maxBinaryMessageSizeString;
    @Value("${websocket.maxBinaryMessageBufferSize:32K}")
    private String maxBinaryMessageBufferSizeString;

    @Value("${websocket.service.classes:}")
    private String serviceClasses;

    /**
     * 默认的构造函数
     */
    public WebsocketServerConfigBean() {
        super(ServerType.WebSocket);
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
     * 获取是否启用ECHO测试
     *
     * @return 返回true表示启用，否则不启用
     */
    public boolean isEchoEnabled() {
        return echoEnabled;
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
     * 获取心跳检测周期
     *
     * @return 检测周期
     */
    public int getPingCycleSec() {
        return pingCycleSec;
    }

    /**
     * 获取清理无效连接的检测周期
     *
     * @return 检测周期
     */
    public int getCleanCycleSec() {
        return cleanCycleSec;
    }

    public WebSocketFilter getWebSocketFilter() {
        return new WebSocketFilter(StringUtils.split(filterRulesString), filterRulesAllows, filterRulesBlocks,
                filterRulesDdosCycleSec, filterRulesDdosMaxConnections);
    }

    /**
     * 获取异步写超时值
     *
     * @return 异步写超时值
     */
    public int getAsyncWriteTimeoutSecs() {
        return asyncWriteTimeoutSecs;
    }

    /**
     * 获取输入缓冲区大小
     *
     * @return 输入缓冲区大小
     */
    public long getInputBufferSize() {
        return TypeUtils.string2Size(inputBufferSizeString, 4 * 1024);
    }

    /**
     * 获取文本消息最大大小
     *
     * @return 文本消息最大大小
     */
    public long getMaxTextMessageSize() {
        return TypeUtils.string2Size(maxTextMessageSizeString, 64 * 1024);
    }

    /**
     * 获取文本消息最大缓冲区大小
     *
     * @return 文本消息最大缓冲区大小
     */
    public long getMaxTextMessageBufferSize() {
        return TypeUtils.string2Size(maxTextMessageBufferSizeString, 32 * 1024);
    }

    /**
     * 获取二进制消息最大大小
     *
     * @return 二进制消息最大大小
     */
    public long getMaxBinaryMessageSize() {
        return TypeUtils.string2Size(maxBinaryMessageSizeString, 64 * 1024);
    }

    /**
     * 获取二进制消息的最大缓冲区大小
     *
     * @return 二进制消息的最大缓冲区大小
     */
    public long getMaxBinaryMessageBufferSize() {
        return TypeUtils.string2Size(maxBinaryMessageBufferSizeString, 32 * 1024);
    }

    /**
     * 获取配置的WebSocket服务类列表
     *
     * @return WebSocket服务类类别
     */
    public String[] getServiceClasses() {
        return StringUtils.split(serviceClasses);
    }

    public class WebSocketFilter {
        private String[] filters;
        private String listAllows, listBlocks;
        private int ddosCycleSec, ddosMaxConnections;

        public WebSocketFilter(String[] filters, String listAllows, String listBlocks, int ddosCycleSec,
                               int ddosMaxConnections) {
            super();
            this.filters = filters;
            this.listAllows = listAllows;
            this.listBlocks = listBlocks;
            this.ddosCycleSec = ddosCycleSec;
            this.ddosMaxConnections = ddosMaxConnections;
        }

        public String[] getFilters() {
            return filters;
        }

        public String getListAllows() {
            return listAllows;
        }

        public String getListBlocks() {
            return listBlocks;
        }

        public int getDdosCycleSec() {
            return ddosCycleSec;
        }

        public int getDdosMaxConnections() {
            return ddosMaxConnections;
        }
    }
}

package org.mx.h2.server;

import org.springframework.beans.factory.annotation.Value;

/**
 * 描述： H2数据库配置对象
 *
 * @author john peng
 * Date time 2018/7/22 下午12:51
 */
public class H2ServerConfigBean {
    @Value("${h2.tcp.enable:false}")
    private boolean tcpEnabled;
    @Value("${h2.tcp.port:9092}")
    private int tcpPort;
    @Value("${h2.tcp.baseDir:./h2}")
    private String tcpBaseDir;
    @Value("${h2.tcp.daemon:true}")
    private boolean tcpDaemon;
    @Value("${h2.tcp.trace:true}")
    private boolean tcpTrace;

    @Value("${h2.web.enable:false}")
    private boolean webEnabled;
    @Value("${h2.web.port:8082}")
    private int webPort;
    @Value("${h2.web.baseDir:./h2}")
    private String webBaseDir;
    @Value("${h2.web.daemon:true}")
    private boolean webDaemon;
    @Value("${h2.web.trace:true}")
    private boolean webTrace;

    @Value("${h2.pg.enable:false}")
    private boolean pgEnabled;
    @Value("${h2.pg.port:5435}")
    private int pgPort;
    @Value("${h2.pg.baseDir:./h2}")
    private String pgBaseDir;
    @Value("${h2.pg.daemon:true}")
    private boolean pgDaemon;
    @Value("${h2.pg.trace:true}")
    private boolean pgTrace;

    public boolean isTcpEnabled() {
        return tcpEnabled;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public String getTcpBaseDir() {
        return tcpBaseDir;
    }

    public boolean isTcpDaemon() {
        return tcpDaemon;
    }

    public boolean isTcpTrace() {
        return tcpTrace;
    }

    public boolean isWebEnabled() {
        return webEnabled;
    }

    public int getWebPort() {
        return webPort;
    }

    public String getWebBaseDir() {
        return webBaseDir;
    }

    public boolean isWebDaemon() {
        return webDaemon;
    }

    public boolean isWebTrace() {
        return webTrace;
    }

    public boolean isPgEnabled() {
        return pgEnabled;
    }

    public int getPgPort() {
        return pgPort;
    }

    public String getPgBaseDir() {
        return pgBaseDir;
    }

    public boolean isPgDaemon() {
        return pgDaemon;
    }

    public boolean isPgTrace() {
        return pgTrace;
    }
}

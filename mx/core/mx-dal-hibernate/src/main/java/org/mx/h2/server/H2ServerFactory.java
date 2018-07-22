package org.mx.h2.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.tools.Server;
import org.mx.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * H2数据库服务器工厂类，用于创建H2数据库的相关服务器，比如：TCP服务器、Web服务器等。
 *
 * @author : john.peng date : 2017/10/8
 */
public class H2ServerFactory {
    private static final Log logger = LogFactory.getLog(H2ServerFactory.class);

    private H2ServerConfigBean h2ServerConfigBean;

    private Server tcpServer;
    private Server webServer;
    private Server pgServer;

    /**
     * 默认的构造函数
     *
     * @param h2ServerConfigBean H2数据库配置对象
     */
    public H2ServerFactory(H2ServerConfigBean h2ServerConfigBean) {
        super();
        this.h2ServerConfigBean = h2ServerConfigBean;
    }

    /**
     * 获取TCP服务器
     *
     * @return 服务器
     */
    public Server getTcpServer() {
        return tcpServer;
    }

    /**
     * 获取WEB服务器
     *
     * @return 服务器
     */
    public Server getWebServer() {
        return webServer;
    }

    /**
     * 获取PG服务器
     *
     * @return 服务器
     */
    public Server getPgServer() {
        return pgServer;
    }

    /**
     * 初始化TCP服务器
     */
    private void initTcpServer() {
        if (h2ServerConfigBean.isTcpEnabled()) {
            List<String> args = new ArrayList<>();
            args.add("-tcpPort");
            args.add(String.valueOf(h2ServerConfigBean.getTcpPort()));
            args.add("-baseDir");
            args.add(h2ServerConfigBean.getTcpBaseDir());
            if (h2ServerConfigBean.isTcpDaemon()) {
                args.add("-tcpDaemon");
            }
            if (h2ServerConfigBean.isTcpTrace()) {
                args.add("-trace");
            }
            try {
                Server server = Server.createTcpServer(args.toArray(new String[0]));
                server.start();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Start H2 TCP Server success, conf: %s.",
                            StringUtils.merge(args, " ")));
                }
                this.tcpServer = server;
            } catch (SQLException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Create H2 TCP server fail, conf: %s.",
                            StringUtils.merge(args, " ")));
                }
            }
        }
    }

    /**
     * 初始化WEB服务器
     */
    private void initWebServer() {
        if (h2ServerConfigBean.isWebEnabled()) {
            List<String> args = new ArrayList<>();
            args.add("-webPort");
            args.add(String.valueOf(h2ServerConfigBean.getWebPort()));
            args.add("-baseDir");
            args.add(h2ServerConfigBean.getWebBaseDir());
            if (h2ServerConfigBean.isWebDaemon()) {
                args.add("-webDaemon");
            }
            if (h2ServerConfigBean.isWebTrace()) {
                args.add("-trace");
            }
            try {
                Server server = Server.createWebServer(args.toArray(new String[0]));
                server.start();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Start H2 Web Server success, conf: %s.",
                            StringUtils.merge(args, " ")));
                }
                this.webServer = server;
            } catch (SQLException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Create H2 Web server fail, conf: %s.",
                            StringUtils.merge(args, " ")));
                }
            }
        }
    }

    /**
     * 初始化PG服务器
     */
    private void initPgServer() {
        if (h2ServerConfigBean.isPgEnabled()) {
            List<String> args = new ArrayList<>();
            args.add("-pgPort");
            args.add(String.valueOf(h2ServerConfigBean.getPgPort()));
            args.add("-baseDir");
            args.add(h2ServerConfigBean.getPgBaseDir());
            if (h2ServerConfigBean.isPgDaemon()) {
                args.add("-pgDaemon");
            }
            if (h2ServerConfigBean.isPgTrace()) {
                args.add("-trace");
            }
            try {
                Server server = Server.createPgServer(args.toArray(new String[0]));
                server.start();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Start H2 pg Server success, conf: %s.",
                            StringUtils.merge(args, " ")));
                }
                this.pgServer = server;
            } catch (SQLException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Create H2 pg server fail, conf: %s.",
                            StringUtils.merge(args, " ")));
                }
            }
        }
    }

    /**
     * 根据配置初始化服务器
     */
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Init the H2 server...");
        }
        initTcpServer();
        initWebServer();
        initPgServer();
        if (logger.isDebugEnabled()) {
            logger.debug("Init the H2 server successfully.");
        }
    }

    /**
     * 关闭创建的服务器
     */
    public void close() {
        if (pgServer != null) {
            pgServer.stop();
            if (logger.isDebugEnabled()) {
                logger.debug("Close H2 pg server successfully.");
            }
        }
        if (webServer != null) {
            webServer.stop();
            if (logger.isDebugEnabled()) {
                logger.debug("Close H2 web server successfully.");
            }
        }
        if (tcpServer != null) {
            tcpServer.stop();
            if (logger.isDebugEnabled()) {
                logger.debug("Close H2 tcp server successfully.");
            }
        }
    }
}

package org.mx.h2.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.tools.Server;
import org.mx.StringUtils;
import org.springframework.core.env.Environment;

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

    private Environment env;

    private Server tcpServer;
    private Server webServer;
    private Server pgServer;

    /**
     * 默认的构造函数
     *
     * @param env 配置信息环境
     */
    public H2ServerFactory(Environment env) {
        super();
        this.env = env;
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
        boolean enable = env.getProperty("h2.tcp.enable", Boolean.class, false);
        if (enable) {
            int port = env.getProperty("h2.tcp.port", Integer.class, 9092);
            String baseDir = env.getProperty("h2.tcp.baseDir", String.class, "~/h2db");
            boolean daemon = env.getProperty("h2.tcp.daemon", Boolean.class, true);
            boolean trace = env.getProperty("h2.tcp.trace", Boolean.class, true);
            List<String> args = new ArrayList<>();
            args.add("-tcpPort");
            args.add(String.valueOf(port));
            //args.add("-baseDir");
            //args.add(baseDir);
            if (daemon) {
                args.add("-tcpDaemon");
            }
            if (trace) {
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
        boolean enable = env.getProperty("h2.web.enable", Boolean.class, false);
        if (enable) {
            int port = env.getProperty("h2.web.port", Integer.class, 9092);
            String baseDir = env.getProperty("h2.web.baseDir", String.class, "~/h2db");
            boolean daemon = env.getProperty("h2.web.daemon", Boolean.class, true);
            boolean trace = env.getProperty("h2.web.trace", Boolean.class, true);
            List<String> args = new ArrayList<>();
            args.add("-webPort");
            args.add(String.valueOf(port));
            args.add("-baseDir");
            args.add(baseDir);
            if (daemon) {
                args.add("-webDaemon");
            }
            if (trace) {
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
        boolean enable = env.getProperty("h2.pg.enable", Boolean.class, false);
        if (enable) {
            int port = env.getProperty("h2.pg.port", Integer.class, 5435);
            String baseDir = env.getProperty("h2.pg.baseDir", String.class, "~/h2db");
            boolean daemon = env.getProperty("h2.pg.daemon", Boolean.class, true);
            boolean trace = env.getProperty("h2.pg.trace", Boolean.class, true);
            List<String> args = new ArrayList<>();
            args.add("-pgPort");
            args.add(String.valueOf(port));
            args.add("-baseDir");
            args.add(baseDir);
            if (daemon) {
                args.add("-pgDaemon");
            }
            if (trace) {
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

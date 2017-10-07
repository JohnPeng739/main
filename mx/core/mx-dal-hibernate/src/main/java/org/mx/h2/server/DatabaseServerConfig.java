package org.mx.h2.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.tools.Server;
import org.mx.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by john on 2017/10/7.
 */
@Configuration
@PropertySource({"classpath:h2-server.properties"})
public class DatabaseServerConfig {
    private static final Log logger = LogFactory.getLog(DatabaseServerConfig.class);

    @Autowired
    private Environment env = null;

    @Bean(name = "h2TcpServer")
    public Server h2TcpServer() {
        boolean enable = env.getProperty("h2.tcp.enable", Boolean.class, false);
        if (enable) {
            int port = env.getProperty("h2.tcp.port", Integer.class, 9092);
            String baseDir = env.getProperty("h2.tcp.baseDir", String.class, "~/h2db");
            boolean daemon = env.getProperty("h2.tcp.daemon",Boolean.class, true);
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
                return server;
            } catch (SQLException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Create H2 TCP server fail, conf: %s.",
                            StringUtils.merge(args, " ")));
                }
            }
        }
        return null;
    }

    @Bean(name = "h2WebServer")
    public Server h2WebServer() {
        boolean enable = env.getProperty("h2.web.enable", Boolean.class, false);
        if (enable) {
            int port = env.getProperty("h2.web.port", Integer.class, 9092);
            String baseDir = env.getProperty("h2.web.baseDir", String.class, "~/h2db");
            boolean daemon = env.getProperty("h2.web.daemon",Boolean.class, true);
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
                return server;
            } catch (SQLException ex) {
                if (logger.isErrorEnabled()) {
                    logger.error(String.format("Create H2 Web server fail, conf: %s.",
                            StringUtils.merge(args, " ")));
                }
            }
        }
        return null;
    }
}

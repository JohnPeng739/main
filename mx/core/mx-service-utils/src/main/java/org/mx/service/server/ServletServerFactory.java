package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.mx.StringUtils;
import org.mx.service.server.servlet.BaseHttpServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * 基于Jetty的Servlet服务器工厂类
 *
 * @author : john.peng created on date : 2017/10/6
 */
public class ServletServerFactory extends AbstractServerFactory {
    private static final Log logger = LogFactory.getLog(ServletServerFactory.class);

    @Autowired
    private Environment env = null;
    @Autowired
    private ApplicationContext context = null;

    /**
     * {@inheritDoc}
     *
     * @see AbstractServerFactory#init()
     */
    @Override
    public void init() throws Exception {
        boolean enabled = env.getProperty("servlet.enabled", Boolean.class, true);
        if (!enabled) {
            // 显式配置enable为false，表示不进行初始化。
            return;
        }
        String serviceClassesDef = "servlet.service.classes";
        String servletServiceClassesDef = this.env.getProperty(serviceClassesDef);
        if (StringUtils.isBlank(servletServiceClassesDef)) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("You not define [%s], will not ", serviceClassesDef));
            }
        } else {
            int port = env.getProperty("servlet.port", Integer.class, 9997);
            Server server = new Server(port);
            ServletContextHandler contextHandler = new ServletContextHandler(1);
            contextHandler.setContextPath("/");
            server.setHandler(contextHandler);
            String[] classesDefs = servletServiceClassesDef.split(",");

            for (String classesDef : classesDefs) {
                if (!StringUtils.isBlank(classesDef)) {
                    List<Class<?>> servletClasses = (List) this.context.getBean(classesDef, List.class);
                    if (servletClasses != null && !servletClasses.isEmpty()) {
                        servletClasses.forEach((clazz) -> {
                            BaseHttpServlet servlet = (BaseHttpServlet) this.context.getBean(clazz);
                            contextHandler.addServlet(new ServletHolder(servlet), servlet.getPath());
                        });
                    }
                }
            }

            server.setStopAtShutdown(true);
            server.setStopTimeout(10);
            super.setServer(server);
            server.start();
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Start ServletServer success, listen port: %d.", port));
            }
        }
    }
}

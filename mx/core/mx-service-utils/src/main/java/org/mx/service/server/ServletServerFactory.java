package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.mx.StringUtils;
import org.mx.service.server.servlet.BaseHttpServlet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * 基于Jetty的Servlet服务器工厂类
 *
 * @author : john.peng created on date : 2017/10/6
 */
@Component("servletServerFactory")
public class ServletServerFactory extends AbstractServerFactory {
    private static final Log logger = LogFactory.getLog(ServletServerFactory.class);

    @Autowired
    private Environment env = null;
    @Autowired
    private ApplicationContext context = null;

    /**
     * {@inheritDoc}
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
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

            boolean security = this.env.getProperty("servlet.security", Boolean.class, false);
            if (security) {
                String keystorePath = env.getProperty("servlet.keystore", "./keystore");
                SslContextFactory sslContextFactory = new SslContextFactory();
                sslContextFactory.setKeyStorePath(keystorePath);
                sslContextFactory.setKeyStorePassword("OBF:1j8x1iup1kfv1j9t1nl91fia1fek1nip1j591kcj1irx1j65");
                sslContextFactory.setKeyManagerPassword("OBF:1k8a1lmp18jj18cg18ce18jj1lj11k5w");
                ServerConnector sslConnector = new ServerConnector(server, sslContextFactory);
                sslConnector.setPort(port);
                server.setConnectors(new Connector[]{sslConnector});
                if (logger.isDebugEnabled()) {
                    logger.debug("The channel is security by ssl.");
                }
            } else {
                //
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

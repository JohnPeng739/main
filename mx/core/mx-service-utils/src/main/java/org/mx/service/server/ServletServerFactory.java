package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.mx.StringUtils;
import org.mx.service.server.servlet.BaseHttpServlet;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * 基于Jetty的Servlet服务器工厂类
 *
 * @author : john.peng created on date : 2017/10/6
 */
public class ServletServerFactory extends HttpServerFactory {
    private static final Log logger = LogFactory.getLog(ServletServerFactory.class);

    private ServletServerConfigBean servletServerConfigBean;
    private ApplicationContext context;

    public ServletServerFactory(ApplicationContext context, ServletServerConfigBean servletServerConfigBean) {
        super(servletServerConfigBean);
        this.context = context;
        this.servletServerConfigBean = servletServerConfigBean;
    }

    /**
     * {@inheritDoc}
     *
     * @see HttpServerFactory#getHandler()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Handler getHandler() {
        String[] classesDefs = servletServerConfigBean.getServiceClasses();
        if (classesDefs == null || classesDefs.length <= 0) {
            if (logger.isWarnEnabled()) {
                logger.warn("You not define any servlet classes.");
            }
            return null;
        } else {
            ServletContextHandler contextHandler = new ServletContextHandler(1);
            contextHandler.setContextPath("/");
            for (String classesDef : classesDefs) {
                if (!StringUtils.isBlank(classesDef)) {
                    List<Class<?>> servletClasses = (List) context.getBean(classesDef, List.class);
                    if (!servletClasses.isEmpty()) {
                        servletClasses.forEach((clazz) -> {
                            BaseHttpServlet servlet = (BaseHttpServlet) context.getBean(clazz);
                            contextHandler.addServlet(new ServletHolder(servlet), servlet.getPath());
                        });
                    }
                }
            }
            return contextHandler;
        }
    }
}

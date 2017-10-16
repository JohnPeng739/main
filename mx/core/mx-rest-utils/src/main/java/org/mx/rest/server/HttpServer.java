package org.mx.rest.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.mx.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * Http REST服务器
 *
 * @author : john.peng date : 2017/10/6
 * @see InitializingBean
 */
@Component("httpServer")
public class HttpServer implements InitializingBean {
    private static final Log logger = LogFactory.getLog(HttpServer.class);

    @Autowired
    private Environment env = null;
    @Autowired
    private ApplicationContext context = null;
    private Server server = null;

    /**
     * 默认的构造函数
     */
    public HttpServer() {
        super();
    }

    /**
     * 获取创建的HTTP服务器
     *
     * @return 服务器
     */
    public Server getServer() {
        return this.server;
    }

    /**
     * {@inheritDoc}
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        String serviceClassesDef = "restful.service.classes";
        String restfulServiceClasses = this.env.getProperty(serviceClassesDef);
        if (StringUtils.isBlank(restfulServiceClasses)) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("You not define [%s], will not create the HttpServer.", serviceClassesDef));
            }

        } else {
            ResourceConfig config = new ResourceConfig();
            String[] classesDefs = restfulServiceClasses.split(",");
            for (String classesDef : classesDefs) {
                if (!StringUtils.isBlank(classesDef)) {
                    List<Class<?>> restfulClasses = (List) this.context.getBean(classesDef, List.class);
                    if (restfulClasses != null && !restfulClasses.isEmpty()) {
                        restfulClasses.forEach((clazz) -> {
                            config.register(this.context.getBean(clazz));
                        });
                    }
                }
            }
            String uri = this.env.getProperty("http.server", "http://localhost:9998/");
            URI baseUri = new URI(uri);
            this.server = JettyHttpContainerFactory.createServer(baseUri, config);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Start HttpServer success, listen base uri: %s.", uri));
            }

        }
    }
}

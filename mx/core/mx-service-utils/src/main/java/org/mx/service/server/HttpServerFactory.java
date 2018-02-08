package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.jetty.JettyHttpContainerProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.UriConnegFilter;
import org.mx.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.util.List;

/**
 * Created by john on 2017/11/4.
 */
public class HttpServerFactory extends AbstractServerFactory {
    private static final Log logger = LogFactory.getLog(HttpServerFactory.class);

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
        boolean enabled = this.env.getProperty("restful.enabled", Boolean.class, true);
        if (!enabled) {
            // 显式配置enable为false，表示不进行初始化。
            return;
        }
        String serviceClassesDef = "restful.service.classes";
        String restfulServiceClasses = this.env.getProperty(serviceClassesDef);
        if (StringUtils.isBlank(restfulServiceClasses)) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("You not define [%s], will not create the HttpServer.", serviceClassesDef));
            }
        } else {
            ResourceConfig config = new ResourceConfig();
            config.register(UriConnegFilter.class);
            config.register(JettyHttpContainerProvider.class);
            String[] classesDefs = restfulServiceClasses.split(",");
            for (String classesDef : classesDefs) {
                if (!StringUtils.isBlank(classesDef)) {
                    List<Class<?>> restfulClasses = (List) this.context.getBean(classesDef, List.class);
                    if (restfulClasses != null && !restfulClasses.isEmpty()) {
                        restfulClasses.forEach((clazz) -> {
                            /**
                             * 如果引入对象实例，将导致Provider接口警告，修改为注册类；
                             * 在后续的Resource类中要引用IoC的Bean，需要使用SpringContextHolder
                             */
                            // config.register(this.context.getBean(clazz));
                            config.register(clazz);
                        });
                    }
                }
            }
            config.property(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
            boolean security = this.env.getProperty("restful.security", Boolean.class, false);
            int port = this.env.getProperty("restful.port", Integer.class, 9999);
            Server server;
            String uri;
            if (security) {
                uri = String.format("https://localhost:%d/", port);
                URI baseUri = new URI(uri);
                String keystorePath = env.getProperty("restful.keystore", "./keystore");
                SslContextFactory sslContextFactory = new SslContextFactory();
                sslContextFactory.setKeyStorePath(keystorePath);
                sslContextFactory.setKeyStorePassword("OBF:1j8x1iup1kfv1j9t1nl91fia1fek1nip1j591kcj1irx1j65");
                sslContextFactory.setKeyManagerPassword("OBF:1k8a1lmp18jj18cg18ce18jj1lj11k5w");
                server = JettyHttpContainerFactory.createServer(baseUri, sslContextFactory, config);
            } else {
                uri = String.format("http://localhost:%d/", port);
                URI baseUri = new URI(uri);
                server = JettyHttpContainerFactory.createServer(baseUri, config);
            }
            server.setStopAtShutdown(true);
            server.setStopTimeout(10);
            super.setServer(server);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Start HttpServer success, listen base uri: %s.", uri));
            }

        }
    }
}

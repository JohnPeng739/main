package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.jetty.JettyHttpContainerProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.UriConnegFilter;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.mx.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * 描述： 基于Jetty的Http服务的工程类
 *
 * @author John.Peng
 * Date time 2018/3/11 上午11:11
 */
@Component("httpServerFactory")
public class HttpServerFactory extends AbstractServerFactory {
    private static final Log logger = LogFactory.getLog(HttpServerFactory.class);

    private Environment env;
    private ApplicationContext context;

    @Autowired
    public HttpServerFactory(Environment env, ApplicationContext context) {
        super();
        this.env = env;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     *
     * @see InitializingBean#afterPropertiesSet()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws Exception {
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
            config.register(RequestContextFilter.class);
            String[] classesDefs = restfulServiceClasses.split(",");
            for (String classesDef : classesDefs) {
                if (!StringUtils.isBlank(classesDef)) {
                    List<Class<?>> restfulClasses = (List) this.context.getBean(classesDef, List.class);
                    if (!restfulClasses.isEmpty()) {
                        /*
                         * 如果引入对象实例，将导致Provider接口警告，修改为注册类；
                         * 然后将ApplicationContext手工注入
                         */
                        restfulClasses.forEach(config::register);
                    }
                }
            }
            /*
             * 为了使用Spring IoC注入，需要将ApplicationContext事先注入
             */
            config.property("contextConfig", context);
            /*
             * 消除了MessageBodyWriter not found for media type=application/json错误。
             */
            // config.property(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
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

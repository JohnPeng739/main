package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.glassfish.jersey.jetty.JettyHttpContainer;
import org.glassfish.jersey.jetty.JettyHttpContainerProvider;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.UriConnegFilter;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.mx.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * 描述： 基于Jetty的Http服务的工程类
 *
 * @author John.Peng
 * Date time 2018/3/11 上午11:11
 */
public class HttpServerFactory extends AbstractServerFactory {
    private static final Log logger = LogFactory.getLog(HttpServerFactory.class);

    private Environment env;
    private ApplicationContext context;

    public HttpServerFactory(Environment env, ApplicationContext context) {
        super();
        this.env = env;
        this.context = context;
    }

    /**
     * {@inheritDoc}
     *
     * @see AbstractServerFactory#init()
     */
    @SuppressWarnings("unchecked")
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
            int threads = this.env.getProperty("restful.threads", Integer.class, 300);
            QueuedThreadPool threadPool = new QueuedThreadPool(threads);
            Server server = new Server(threadPool);
            server.addBean(new ScheduledExecutorScheduler());
            HttpConfiguration httpConfiguration = new HttpConfiguration();
            httpConfiguration.setOutputBufferSize(this.env.getProperty("restful.outputSize", Integer.class, 32768));
            httpConfiguration.setRequestHeaderSize(this.env.getProperty("restful.requestHeaderSize", Integer.class, 8192));
            httpConfiguration.setResponseHeaderSize(this.env.getProperty("restful.responseHeaderSize", Integer.class, 8192));
            httpConfiguration.setSendServerVersion(true);
            httpConfiguration.setSendDateHeader(false);
            JettyHttpContainer container = ContainerFactory.createContainer(JettyHttpContainer.class, config);
            server.setHandler(container);
            String uri;
            if (security) {
                httpConfiguration.setSecureScheme("https");
                httpConfiguration.setSecurePort(port);
                httpConfiguration.addCustomizer(new SecureRequestCustomizer());

                String keystorePath = env.getProperty("restful.keystore", "./keystore");
                SslContextFactory sslContextFactory = new SslContextFactory();
                sslContextFactory.setKeyStorePath(keystorePath);
                sslContextFactory.setKeyStorePassword("OBF:1j8x1iup1kfv1j9t1nl91fia1fek1nip1j591kcj1irx1j65");
                sslContextFactory.setKeyManagerPassword("OBF:1k8a1lmp18jj18cg18ce18jj1lj11k5w");

                ServerConnector https = new ServerConnector(server,
                        new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.toString()),
                        new HttpConnectionFactory(httpConfiguration));
                https.setPort(port);
                server.addConnector(https);
            } else {
                ServerConnector http = new ServerConnector(server,
                        new HttpConnectionFactory(httpConfiguration));
                http.setPort(port);
                http.setIdleTimeout(30000);
                server.addConnector(http);
            }
            server.setDumpAfterStart(false);
            server.setDumpBeforeStop(false);
            server.setStopAtShutdown(true);
            server.setStopTimeout(10);
            server.start();
            super.setServer(server);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Start HttpServer success, listen : %d, security: %s.", port, security));
            }
        }
    }
}

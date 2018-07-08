package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.mx.TypeUtils;
import org.springframework.core.env.Environment;

/**
 * 描述： 基于Jetty的Http服务的工程类
 *
 * @author John.Peng
 * Date time 2018/3/11 上午11:11
 */
public abstract class HttpServerFactory extends AbstractServerFactory {
    private static final Log logger = LogFactory.getLog(HttpServerFactory.class);

    private Environment env;
    private String configPrefix;

    public HttpServerFactory(Environment env, String configPrefix) {
        super();
        this.env = env;
        this.configPrefix = configPrefix;
    }

    /**
     * 获取本Server特定的Handler
     *
     * @return Handler
     */
    protected abstract Handler getHandler();

    /**
     * {@inheritDoc}
     *
     * @see AbstractServerFactory#init()
     */
    public void init() throws Exception {
        boolean enabled = this.env.getProperty(String.format("%s.enabled", configPrefix), Boolean.class, true);
        if (!enabled) {
            // 显式配置enable为false，表示不进行初始化。
            return;
        }

        long idleTimeout = env.getProperty(String.format("%s.idleTimeoutSecs", configPrefix), Long.class, 300L) * 1000;
        int port = env.getProperty(String.format("%s.port", configPrefix), Integer.class, 9999);
        int threads = env.getProperty(String.format("%s.threads", configPrefix), Integer.class, 300);
        long outputBufferSize = TypeUtils.string2Size(
                env.getProperty(String.format("%s.outputSize", configPrefix)), 32768);
        long requestHeaderSize = TypeUtils.string2Size(
                env.getProperty(String.format("%s.requestHeaderSize", configPrefix)), 8192);
        long responseHeaderSize = TypeUtils.string2Size(
                env.getProperty(String.format("%s.responseHeaderSize", configPrefix)), 8192);
        boolean security = env.getProperty(String.format("%s.security", configPrefix), Boolean.class, false);
        String keystorePath = env.getProperty(String.format("%s.security.keystore", configPrefix), "./keystore");
        String keystorePassword = env.getProperty(String.format("%s.security.keystorePassword", configPrefix),
                "OBF:1j8x1iup1kfv1j9t1nl91fia1fek1nip1j591kcj1irx1j65");
        String keyManagerPassword = env.getProperty(String.format("%s.security.keyManagerPassword", configPrefix),
                "OBF:1k8a1lmp18jj18cg18ce18jj1lj11k5w");

        Handler handler = getHandler();
        if (handler == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("There are not a handler, the server can't start.");
            }
        } else {
            QueuedThreadPool threadPool = new QueuedThreadPool(threads);
            Server server = new Server(threadPool);
            server.addBean(new ScheduledExecutorScheduler());
            server.setHandler(handler);
            HttpConfiguration httpConfiguration = new HttpConfiguration();
            httpConfiguration.setOutputBufferSize((int) outputBufferSize);
            httpConfiguration.setRequestHeaderSize((int) requestHeaderSize);
            httpConfiguration.setResponseHeaderSize((int) responseHeaderSize);
            httpConfiguration.setSendServerVersion(true);
            httpConfiguration.setSendDateHeader(false);
            String uri;
            if (security) {
                // 创建HTTPS
                httpConfiguration.setSecureScheme("https");
                httpConfiguration.setSecurePort(port);
                httpConfiguration.addCustomizer(new SecureRequestCustomizer());

                SslContextFactory sslContextFactory = new SslContextFactory();
                sslContextFactory.setKeyStorePath(keystorePath);
                sslContextFactory.setKeyStorePassword(keystorePassword);
                sslContextFactory.setKeyManagerPassword(keyManagerPassword);

                ServerConnector https = new ServerConnector(server,
                        new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.toString()),
                        new HttpConnectionFactory(httpConfiguration));
                https.setPort(port);
                https.setIdleTimeout(idleTimeout);
                server.addConnector(https);
            } else {
                // 创建HTTP
                ServerConnector http = new ServerConnector(server,
                        new HttpConnectionFactory(httpConfiguration));
                http.setPort(port);
                http.setIdleTimeout(idleTimeout);
                server.addConnector(http);
            }
            server.setDumpAfterStart(false);
            server.setDumpBeforeStop(false);
            server.setStopAtShutdown(true);
            server.setStopTimeout(10);
            server.start();
            super.setServer(server);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Start Server[%s] success, listen : %d, security: %s.", configPrefix, port, security));
            }
        }
    }
}

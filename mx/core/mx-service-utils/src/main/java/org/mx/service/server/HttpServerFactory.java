package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;

/**
 * 描述： 基于Jetty的Http服务的工程类
 *
 * @author John.Peng
 * Date time 2018/3/11 上午11:11
 */
public abstract class HttpServerFactory extends AbstractServerFactory {
    private static final Log logger = LogFactory.getLog(HttpServerFactory.class);

    private HttpServerConfigBean httpServerConfigBean;

    public HttpServerFactory(HttpServerConfigBean httpServerConfigBean) {
        super();
        this.httpServerConfigBean = httpServerConfigBean;
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
        if (!httpServerConfigBean.isEnabled()) {
            // 显式配置enable为false，表示不进行初始化。
            return;
        }

        Handler handler = getHandler();
        if (handler == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("There are not a handler, the server can't start.");
            }
        } else {
            QueuedThreadPool threadPool = new QueuedThreadPool(httpServerConfigBean.getThreads());
            Server server = new Server(threadPool);
            server.addBean(new ScheduledExecutorScheduler());
            server.setHandler(handler);
            HttpConfiguration httpConfiguration = new HttpConfiguration();
            httpConfiguration.setOutputBufferSize((int) httpServerConfigBean.getOutputSize());
            httpConfiguration.setRequestHeaderSize((int) httpServerConfigBean.getRequestHeaderSize());
            httpConfiguration.setResponseHeaderSize((int) httpServerConfigBean.getResponseHeaderSize());
            httpConfiguration.setSendServerVersion(true);
            httpConfiguration.setSendDateHeader(false);
            String uri;
            if (httpServerConfigBean.isSecurity()) {
                // 创建HTTPS
                httpConfiguration.setSecureScheme("https");
                httpConfiguration.setSecurePort(httpServerConfigBean.getPort());
                httpConfiguration.addCustomizer(new SecureRequestCustomizer());

                SslContextFactory sslContextFactory = new SslContextFactory();
                sslContextFactory.setKeyStoreType(httpServerConfigBean.getKeystoreType());
                sslContextFactory.setKeyStorePath(httpServerConfigBean.getKeystorePath());
                sslContextFactory.setTrustStorePath(httpServerConfigBean.getKeystorePath());
                sslContextFactory.setKeyStorePassword(httpServerConfigBean.getKeystorePassword());
                sslContextFactory.setKeyManagerPassword(httpServerConfigBean.getKeyManagerPassword());
                sslContextFactory.setTrustStorePassword(httpServerConfigBean.getKeystorePassword());

                ServerConnector https = new ServerConnector(server,
                        new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.toString()),
                        new HttpConnectionFactory(httpConfiguration));
                https.setPort(httpServerConfigBean.getPort());
                https.setIdleTimeout(httpServerConfigBean.getIdleTimeoutSecs());
                server.addConnector(https);
            } else {
                // 创建HTTP
                ServerConnector http = new ServerConnector(server,
                        new HttpConnectionFactory(httpConfiguration));
                http.setPort(httpServerConfigBean.getPort());
                http.setIdleTimeout(httpServerConfigBean.getIdleTimeoutSecs());
                server.addConnector(http);
            }
            server.setDumpAfterStart(false);
            server.setDumpBeforeStop(false);
            server.setStopAtShutdown(true);
            server.setStopTimeout(10);
            server.start();
            super.setServer(server);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Start Server[%s] success, listen : %d, security: %s.",
                        httpServerConfigBean.getServerType(), httpServerConfigBean.getPort(),
                        httpServerConfigBean.isSecurity()));
            }
        }
    }
}

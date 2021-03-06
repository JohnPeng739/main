package org.mx.service.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;

/**
 * 定义Jetty相关服务器的抽象工厂类。
 *
 * @author : john.peng created on date : 2017/11/4
 */
public abstract class AbstractServerFactory {
    private static final Log logger = LogFactory.getLog(AbstractServerFactory.class);
    private Server server = null;

    /**
     * 获取创建的服务器对象
     *
     * @return 服务器对象
     */
    public Server getServer() {
        return server;
    }

    /**
     * 设置服务器对象，由子类的init方法调用。
     *
     * @param server 服务器对象
     */
    protected void setServer(Server server) {
        this.server = server;
    }

    /**
     * 初始化服务器工厂
     *
     * @throws Exception 初始化过程中发生的异常
     */
    public abstract void init() throws Exception;

    /**
     * 销毁服务器工厂
     */
    public void destroy() {
        if (server != null) {
            try {
                server.stop();
                server.destroy();
                if (logger.isInfoEnabled()) {
                    logger.info("Close the server successfully.");
                }
            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("Close the server fail.", ex);
                }
            }
        }
    }
}

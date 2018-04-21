package org.mx.hanlp.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.hanlp.config.SuggesterConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 描述： 推荐服务器运行入口<br>
 * 服务器启动之后可以使用Restful方式对服务进行控制：关闭、重载、清除
 *
 * @author John.Peng
 *         Date time 2018/4/20 上午8:43
 */
public class SuggesterServer {
    private static final Log logger = LogFactory.getLog(SuggesterServer.class);

    private static AnnotationConfigApplicationContext context = null;

    public static void main(String[] args) {
        startServer();
    }

    private static void startServer() {
        stopServer();
        context = new AnnotationConfigApplicationContext(SuggesterConfig.class);
        if (logger.isInfoEnabled()) {
            logger.info("Start the suggester server successfully.");
        }
    }

    public static void stopServer() {
        if (context != null) {
            context.close();
            if (logger.isInfoEnabled()) {
                logger.info("Stop the suggester server successfully.");
            }
        }
    }
}

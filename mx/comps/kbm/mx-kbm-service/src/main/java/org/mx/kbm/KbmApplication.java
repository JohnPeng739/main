package org.mx.kbm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.kbm.config.KbmApplicationConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 描述： 知识库管理系统服务运行入口
 *
 * @author john peng
 * Date time 2018/5/6 下午2:04
 */
public class KbmApplication {
    private static final Log logger = LogFactory.getLog(KbmApplication.class);

    private static AnnotationConfigApplicationContext context = null;

    public static void main(String[] args) {
        KbmApplication.startServer();
    }

    public static void startServer() {
        context = new AnnotationConfigApplicationContext(KbmApplicationConfig.class);
        if (logger.isInfoEnabled()) {
            logger.info("Start the KBM server successfully.");
        }
    }

    public static void stopServer() {
        if (context != null) {
            context.close();
            if (logger.isInfoEnabled()) {
                logger.info("Close the KBM server successfully.");
            }
        }
    }
}

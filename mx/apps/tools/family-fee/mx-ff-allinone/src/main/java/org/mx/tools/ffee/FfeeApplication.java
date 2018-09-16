package org.mx.tools.ffee;

import org.mx.tools.ffee.config.FfeeAppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 描述： FFEE后台应用启动程序
 *
 * @author John.Peng
 * Date time 2018/2/19 下午5:15
 */
public class FfeeApplication {
    private static AnnotationConfigApplicationContext context;

    public static void main(String[] args) {
        context = new AnnotationConfigApplicationContext(FfeeAppConfig.class);
    }

    public static void stopServer() {
        if (context != null) {
            context.close();
        }
    }
}

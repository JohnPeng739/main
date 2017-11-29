package org.mx.h2.server;

import org.mx.h2.server.config.DatabaseServerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 启动H2数据库服务器，依赖DatabaseServerConfig运行.
 *
 * @author : john.peng created on date : 2017/11/29
 */
public class H2ServerApplication {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DatabaseServerConfig.class);
        System.out.println("Press any key to close...");
        System.in.read();
        context.close();
    }
}

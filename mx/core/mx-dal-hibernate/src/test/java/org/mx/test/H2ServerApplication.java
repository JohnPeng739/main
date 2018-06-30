package org.mx.test;

import org.mx.test.config.TestH2ServerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 启动H2数据库服务器，依赖DatabaseServerConfig运行.
 *
 * @author : john.peng created on date : 2017/11/29
 */
public class H2ServerApplication {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestH2ServerConfig.class);
        System.out.println("Press any key to close...");
        System.in.read();
        context.close();
    }
}

package org.mx.test.h2;

import org.h2.tools.Server;
import org.mx.h2.server.config.DatabaseServerConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by john on 2017/10/7.
 */
public class StartServer {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DatabaseServerConfig.class);
        System.out.println("Press any key to shutdown server");
        System.in.read();
        context.close();
    }
}

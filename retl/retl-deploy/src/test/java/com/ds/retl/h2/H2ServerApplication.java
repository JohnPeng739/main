package com.ds.retl.h2;

import org.mx.h2.server.config.DatabaseServerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by john on 2017/10/8.
 */
public class H2ServerApplication {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DatabaseServerConfig.class);
        System.out.println("Press any key to close...");
        System.in.read();
        context.close();
    }
}

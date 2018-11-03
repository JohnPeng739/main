package org.mx.service;

import org.mx.service.test.config.TestConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestService {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                TestConfig.class
        );
        System.out.println("Start the test server successfully.");
        if (args.length == 1 && args[0].equalsIgnoreCase("client")) {
            System.out.println("Print any key for exit.");
            System.in.read();
            context.close();
        }
    }
}

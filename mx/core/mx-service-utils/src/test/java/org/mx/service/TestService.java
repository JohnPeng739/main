package org.mx.service;

import org.mx.service.test.config.TestWebsocketConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestService {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestWebsocketConfig.class);
        System.in.read();
        context.close();
    }
}

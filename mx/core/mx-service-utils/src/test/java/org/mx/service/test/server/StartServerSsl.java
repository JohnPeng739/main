package org.mx.service.test.server;

import org.mx.service.test.server.config.TestConfigSsl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by john on 2018/1/10.
 */
public class StartServerSsl {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(TestConfigSsl.class);
    }
}

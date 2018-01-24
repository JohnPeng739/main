package org.mx.service.server;

import org.mx.service.server.config.TestConfig;
import org.mx.service.server.config.TestConfigSsl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by john on 2018/1/10.
 */
public class StartServerSsl {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(TestConfigSsl.class);
    }
}

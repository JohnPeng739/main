package org.mx.comps.notify;

import org.mx.comps.notify.config.TestConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by john on 2018/1/10.
 */
public class RunServer {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(TestConfig.class);
    }
}

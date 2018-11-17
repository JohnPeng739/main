package org.mx.service.boot;

import org.mx.service.SpringBootUtils;
import org.mx.service.test.config.TestConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({TestConfig.class})
public class TestBootApplication {
    public static void main(String[] args) throws Exception {
        SpringBootUtils utils = new SpringBootUtils();
        utils.startApplication(TestBootApplication.class, args);
        System.out.println("please any key to exit.");
        System.in.read();
        utils.stopApplication();
    }
}

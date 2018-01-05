package org.mx.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 引入Spring相关Bean的配置类
 *
 * @author : john.peng created on date : 2018/1/5
 */
@Configuration
@ComponentScan({
        "org.mx.spring"
})
public class SpringConfig {
}

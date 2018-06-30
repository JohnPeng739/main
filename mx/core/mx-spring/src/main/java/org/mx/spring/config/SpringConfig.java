package org.mx.spring.config;

import org.mx.spring.utils.SpringContextHolder;
import org.springframework.context.annotation.Bean;

/**
 * 引入Spring相关Bean的配置类
 *
 * @author : john.peng created on date : 2018/1/5
 */
public class SpringConfig {
    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
}

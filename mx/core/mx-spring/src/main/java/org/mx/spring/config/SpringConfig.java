package org.mx.spring.config;

import org.mx.spring.session.SessionDataStore;
import org.mx.spring.session.impl.SessionDataThreadLocal;
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

    /**
     * 基于线程局部堆方式创建SessionDataStore
     *
     * @return SessionDataStore对象
     */
    @Bean(name = "sessionDataStore")
    public SessionDataStore sessionDataThreadLocal() {
        return new SessionDataThreadLocal();
    }
}

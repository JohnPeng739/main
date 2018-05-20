package org.mx.spring.config;

import org.mx.spring.InitializeTaskFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * 引入Spring相关Bean的配置类
 *
 * @author : john.peng created on date : 2018/1/5
 */
@ComponentScan({
        "org.mx.spring.utils"
})
public class SpringConfig {
    /**
     * 创建初始化任务工厂
     *
     * @param context Spring IoC上下文
     * @return 初始化任务工厂
     */
    @Bean(name = "initializeTaskFactory", initMethod = "init", destroyMethod = "destroy")
    public InitializeTaskFactory initializeTaskFactory(ApplicationContext context) {
        return new InitializeTaskFactory(context);
    }
}

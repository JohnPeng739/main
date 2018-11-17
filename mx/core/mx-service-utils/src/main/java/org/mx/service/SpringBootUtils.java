package org.mx.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 描述： Spring boot应用启动工具类，添加常规服务初始化自动配置。
 *
 * @author john peng
 * Date time 2018/11/16 10:04 PM
 */
public class SpringBootUtils {
    private ConfigurableApplicationContext context;

    /**
     * 启动Spring boot应用
     *
     * @param clazz 入口类
     * @param args  启动参数
     */
    public void startApplication(Class<?> clazz, String[] args) {
        SpringApplication app = new SpringApplication(clazz);
        app.addInitializers(context -> {
            this.context = context;
            // TODO 根据启动类初始化相关内容：RESTful
        });
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    /**
     * 停止Spring boot应用
     */
    public void stopApplication() {
        if (context != null) {
            context.close();
        }
    }
}

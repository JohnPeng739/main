package com.ds.retl;

import com.ds.retl.rest.config.RETLRestConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 运行实时ETL的服务平台的应用程序
 *
 * @author : john.peng created on date : 2017/10/6
 */
public class RETLRestApplication {
    /**
     * 运行实时ETL的服务平台的入口
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(RETLRestConfig.class);
    }
}

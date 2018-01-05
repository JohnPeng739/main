package org.mx.dal.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * DAL（数据访问层）Java Configure类
 * <p>
 * 扫描包：org.mx.dal.session.impl中的组件
 * 加载配置文件：classpath:database.properties
 *
 * @author : john.peng date : 2017/10/7
 */
@Configuration
@ComponentScan({
        "org.mx.dal.session.impl"
})
public class DalConfig {
}

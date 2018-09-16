package org.mx.tools.ffee.config;

import org.mx.dal.config.DalConfig;
import org.mx.service.server.config.ServerConfig;
import org.mx.tools.ffee.rest.AccountManageResource;
import org.mx.tools.ffee.rest.CourseManageResource;
import org.mx.tools.ffee.rest.FamilyManageResource;
import org.mx.tools.ffee.rest.WxAuthorizationResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

/**
 * 描述： FFEE应用中Restful服务配置类。
 *
 * @author John.Peng
 * Date time 2018/2/18 上午11:57
 */
@Import({
        DalConfig.class,
        ServerConfig.class
})
@ComponentScan({
        "org.mx.tools.ffee.restful",
        "org.mx.tools.ffee.service.impl"
})
public class FfeeConfig {
    @Bean(name = "ffeeConfigBean")
    public FfeeConfigBean ffeeConfigBean() {
        return new FfeeConfigBean();
    }

    @Bean(name = "restfulClassesFFEE")
    public List<Class<?>> ffeeRestfulClasses() {
        return Arrays.asList(
                WxAuthorizationResource.class,
                AccountManageResource.class,
                FamilyManageResource.class,
                CourseManageResource.class
        );
    }
}

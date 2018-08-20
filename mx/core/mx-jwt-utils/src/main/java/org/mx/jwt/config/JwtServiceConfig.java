package org.mx.jwt.config;

import org.mx.jwt.service.impl.JwtServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * 描述： JWT(JSON Web Tokens)服务配置类
 *
 * @author john peng
 * Date time 2018/8/19 下午7:19
 */
public class JwtServiceConfig {
    @Bean
    public AuthConfigBean authConfigBean(Environment env) {
        return new AuthConfigBean(env);
    }

    @Bean(name = "jwtService", initMethod = "init", destroyMethod = "destroy")
    public JwtServiceImpl jwtService(AuthConfigBean authConfigBean) {
        return new JwtServiceImpl(authConfigBean);
    }
}

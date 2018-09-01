package org.mx.service.rest.cors;

import org.springframework.beans.factory.annotation.Value;

/**
 * 描述： 跨域配置信息类定义
 *
 * @author john peng
 * Date time 2018/9/1 下午1:32
 */
public class CorsConfigBean {
    @Value("${cors.enabled:false}")
    private boolean enabled;
    @Value("${cors.allow.origin:*}")
    private String allowOrigin;
    @Value("${cors.allow.headers:origin, content-type, accept, authorization}")
    private String allowHeaders;
    @Value("${cors.expose.headers:}")
    private String exposeHeader;
    @Value("${cors.allow.credentials:true}")
    private String allowCredentials;
    @Value("${cors.allow.methods:GET, POST, PUT, DELETE, OPTIONS, HEAD}")
    private String allowMethods;
    @Value("${cors.max.age:1209600}")
    private String maxAge;

    public boolean isEnabled() {
        return enabled;
    }

    public String getAllowOrigin() {
        return allowOrigin;
    }

    public String getAllowHeaders() {
        return allowHeaders;
    }

    public String getExposeHeader() {
        return exposeHeader;
    }

    public String getAllowCredentials() {
        return allowCredentials;
    }

    public String getAllowMethods() {
        return allowMethods;
    }

    public String getMaxAge() {
        return maxAge;
    }
}

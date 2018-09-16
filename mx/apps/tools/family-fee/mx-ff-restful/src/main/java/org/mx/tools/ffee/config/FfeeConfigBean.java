package org.mx.tools.ffee.config;

import org.springframework.beans.factory.annotation.Value;

public class FfeeConfigBean {
    @Value("${wx.ff.appid:}")
    private String appId;
    @Value("${wx.ff.security:}")
    private String security;
    @Value("${wx.ff.token:hexiedeyijia740173}")
    private String token;
    @Value("${wx.api.url:https://api.weixin.qq.com/sns/jscode2session}")
    private String apiUrl;

    public String getAppId() {
        return appId;
    }

    public String getSecurity() {
        return security;
    }

    public String getToken() {
        return token;
    }

    public String getApiUrl() {
        return apiUrl;
    }
}

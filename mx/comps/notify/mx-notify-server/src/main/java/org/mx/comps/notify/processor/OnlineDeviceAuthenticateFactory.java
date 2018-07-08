package org.mx.comps.notify.processor;

import org.mx.comps.notify.online.OnlineDeviceAuthenticate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 描述： 在线设备身份鉴别工厂
 *
 * @author john peng
 * Date time 2018/7/8 下午2:50
 */
@Component
public class OnlineDeviceAuthenticateFactory {
    private ApplicationContext context;

    @Autowired
    public OnlineDeviceAuthenticateFactory(ApplicationContext context) {
        super();
        this.context = context;
    }

    /**
     * 获取IoC容器中配置的在线设备鉴别器
     *
     * @return 设备鉴别器集合
     */
    public Map<String, OnlineDeviceAuthenticate> getAuthenticates() {
        return context.getBeansOfType(OnlineDeviceAuthenticate.class);
    }
}

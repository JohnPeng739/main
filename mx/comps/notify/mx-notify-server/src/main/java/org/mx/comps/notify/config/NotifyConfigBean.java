package org.mx.comps.notify.config;

import org.mx.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * 描述： 消息推送服务器配置对象
 *
 * @author john peng
 * Date time 2018/7/22 下午1:19
 */
public class NotifyConfigBean {
    @Value("${websocket.notify.path:/notify}")
    private String notifyPath;
    @Value("${websocket.notify.processors:}")
    private String processorsString;
    @Value("${websocket.notify.notifyListener:}")
    private String notifyListener;
    @Value("${websocket.notify.authenticate:}")
    private String authenticate;
    @Value("${websocket.notify.device.idleTimeoutSecs:-1}")
    private int deviceIdleTimeoutSecs;

    public String getNotifyPath() {
        return notifyPath;
    }

    public String[] getProcessors() {
        return StringUtils.split(processorsString);
    }

    public String getNotifyListener() {
        return notifyListener;
    }

    public String getAuthenticate() {
        return authenticate;
    }

    public int getDeviceIdleTimeoutSecs() {
        return deviceIdleTimeoutSecs;
    }
}

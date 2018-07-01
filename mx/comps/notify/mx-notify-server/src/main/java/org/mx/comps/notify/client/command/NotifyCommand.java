package org.mx.comps.notify.client.command;

import org.mx.comps.notify.client.NotifyBean;
import org.mx.comps.notify.processor.MessageProcessorChain;

/**
 * 通知命令
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class NotifyCommand<T> extends BaseCommand {
    private NotifyBean<T> data;

    @SuppressWarnings("unchecked")
    public NotifyCommand(String src, String deviceId, NotifyBean.TarType tarType, String tar, long expiredTime, T message) {
        super("notify", MessageProcessorChain.TYPE_USER);
        this.data = new NotifyBean(src, deviceId, tarType, tar, expiredTime, message);
    }

    public NotifyBean<T> getData() {
        return data;
    }

    public void setData(NotifyBean<T> data) {
        this.data = data;
    }
}

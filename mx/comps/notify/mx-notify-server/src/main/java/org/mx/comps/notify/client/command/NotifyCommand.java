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

    public NotifyCommand(String src, NotifyBean.TarType tarType, String tar, long expiredTime, boolean needAck, T message) {
        super("notify", MessageProcessorChain.TYPE_USER);
        this.data = new NotifyBean(src, tarType, tar, expiredTime, needAck, message);
    }

    public NotifyBean<T> getData() {
        return data;
    }

    public void setData(NotifyBean<T> data) {
        this.data = data;
    }
}

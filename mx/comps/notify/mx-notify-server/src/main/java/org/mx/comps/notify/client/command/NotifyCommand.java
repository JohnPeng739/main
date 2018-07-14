package org.mx.comps.notify.client.command;

import org.mx.comps.notify.client.NotifyBean;

/**
 * 通知命令
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class NotifyCommand<T> extends Command {
    private static String messageId = "notify", messageVersion = "1.0";

    /**
     * 构造函数
     *
     * @param src         推送源
     * @param deviceId    设备号
     * @param tarType     推送目标类型
     * @param tar         推送目标
     * @param expiredTime 过期时间
     * @param message     推送消息数据
     * @see org.mx.comps.notify.client.NotifyBean.TarType
     */
    public NotifyCommand(String src, String deviceId, NotifyBean.TarType tarType, String tar, long expiredTime, T message) {
        this(messageId, messageVersion, src, deviceId, tarType, tar, expiredTime, message);
    }

    /**
     * 构造函数
     *
     * @param messageId      消息号
     * @param messageVersion 消息版本
     * @param src            推送源
     * @param deviceId       设备号
     * @param tarType        推送目标类型
     * @param tar            推送目标
     * @param expiredTime    过期时间
     * @param message        推送消息数据
     * @see org.mx.comps.notify.client.NotifyBean.TarType
     */
    public NotifyCommand(String messageId, String messageVersion, String src, String deviceId,
                         NotifyBean.TarType tarType, String tar, long expiredTime, T message) {
        super("notify", CommandType.USER);
        super.setMessage(new Message<>(messageId, messageVersion,
                new NotifyBean<>(src, deviceId, tarType, tar, expiredTime, message)));
    }
}

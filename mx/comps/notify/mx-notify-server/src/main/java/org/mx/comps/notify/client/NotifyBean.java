package org.mx.comps.notify.client;

/**
 * 推送通知对象定义
 *
 * @author : john.peng created on date : 2018/1/9
 */
public class NotifyBean<T> {
    private String src, tar;
    private TarType tarType = TarType.Devices;
    private long expiredTime;
    private boolean needAck;
    private T message;

    /**
     * 默认的构造函数
     */
    public NotifyBean() {
        super();
    }

    /**
     * 默认的构造函数
     *
     * @param src         源
     * @param tarType     目标类型
     * @param tar         目标
     * @param expiredTime 过期时间
     * @param needAck     是否需要确认
     * @param message     业务消息对象
     */
    public NotifyBean(String src, TarType tarType, String tar, long expiredTime, boolean needAck, T message) {
        super();
        this.src = src;
        this.tarType = tarType;
        this.tar = tar;
        this.expiredTime = expiredTime;
        this.needAck = needAck;
        this.message = message;
    }

    /**
     * 源
     *
     * @return 源
     */
    public String getSrc() {
        return src;
    }

    /**
     * 设置源
     *
     * @param src 源
     */
    public void setSrc(String src) {
        this.src = src;
    }

    /**
     * 目标
     *
     * @return 目标
     */
    public String getTar() {
        return tar;
    }

    /**
     * 设置目标
     *
     * @param tar 目标
     */
    public void setTar(String tar) {
        this.tar = tar;
    }

    /**
     * 获取目标类型，支持：设备范围、IP范围、状态、晚于/早于注册时间
     *
     * @return 目标类型
     */
    public TarType getTarType() {
        return tarType;
    }

    /**
     * 设置目标类型
     *
     * @param tarType 目标类
     */
    public void setTarType(TarType tarType) {
        this.tarType = tarType;
    }

    /**
     * 获取消息过期时间，单位毫秒
     *
     * @return 过期时间
     */
    public long getExpiredTime() {
        return expiredTime;
    }

    /**
     * 设置过期时间
     *
     * @param expiredTime 过期时间，永远不过期设置为-1
     */
    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    /**
     * 获取是否需要确认
     *
     * @return 需要确认返回true，否则返回false
     */
    public boolean isNeedAck() {
        return needAck;
    }

    /**
     * 设置是否需要确认
     *
     * @param needAck 需要确认设置为true，否则设置为false
     */
    public void setNeedAck(boolean needAck) {
        this.needAck = needAck;
    }

    /**
     * 业务消息对象，支持泛型
     *
     * @return 业务消息对象
     */
    public T getMessage() {
        return message;
    }

    /**
     * 设置业务消息对象
     *
     * @param message 业务消息对象
     */
    public void setMessage(T message) {
        this.message = message;
    }

    // 目标类型，支持：设备ID列表、IP列表、状态类别、晚于注册时间、早于注册时间等。
    public enum TarType {
        Devices, IPs, States, Later, Early
    }
}

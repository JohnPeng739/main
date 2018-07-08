package org.mx.comps.notify.online;

/**
 * 在线设备对象定义
 *
 * @author : john.peng created on date : 2018/1/3
 */
public class OnlineDevice {
    private String deviceId, state, connectKey, extraData;
    private long registryTime, lastTime;
    private double lastLongitude, lastLatitude;

    /**
     * 更新设备信息
     *
     * @param state         状态
     * @param lastTime      时间
     * @param lastLongitude 经度
     * @param lastLatitude  纬度
     */
    public void update(String state, long lastTime, double lastLongitude, double lastLatitude) {
        this.state = state;
        this.lastTime = lastTime;
        this.lastLongitude = lastLongitude;
        this.lastLatitude = lastLatitude;
    }

    /**
     * 获取设备ID
     *
     * @return 设备ID
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 设置设备ID
     *
     * @param deviceId 设备ID
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 获取状态
     *
     * @return 状态
     */
    public String getState() {
        return state;
    }

    /**
     * 设置状态
     *
     * @param state 状态
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 获取连接关键字
     *
     * @return 连接关键字
     */
    public String getConnectKey() {
        return connectKey;
    }

    /**
     * 设置连接关键字
     *
     * @param connectKey 连接关键字
     */
    public void setConnectKey(String connectKey) {
        this.connectKey = connectKey;
    }

    /**
     * 获取注册时间
     *
     * @return 注册时间
     */
    public long getRegistryTime() {
        return registryTime;
    }

    /**
     * 设置注册时间
     *
     * @param registryTime 时间
     */
    public void setRegistryTime(long registryTime) {
        this.registryTime = registryTime;
    }

    /**
     * 获取时间
     *
     * @return 时间
     */
    public long getLastTime() {
        return lastTime;
    }

    /**
     * 设置时间
     *
     * @param lastTime 时间
     */
    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    /**
     * 获取经度
     *
     * @return 经度
     */
    public double getLastLongitude() {
        return lastLongitude;
    }

    /**
     * 设置经度
     *
     * @param lastLongitude 经度
     */
    public void setLastLongitude(double lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    /**
     * 获取纬度
     *
     * @return 纬度
     */
    public double getLastLatitude() {
        return lastLatitude;
    }

    /**
     * 设置纬度
     *
     * @param lastLatitude 纬度
     */
    public void setLastLatitude(double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    /**
     * 获取附加数据字串
     *
     * @return 附加数据字串
     */
    public String getExtraData() {
        return extraData;
    }

    /**
     * 设置附加数据字串
     *
     * @param extraData 附加数据字串
     */
    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}

package org.mx.comps.notify.online;

/**
 * 在线设备对象定义
 *
 * @author : john.peng created on date : 2018/1/3
 */
public class OnlineDevice {
    private String deviceId, state, connectKey;
    private long lastTime;
    private double lastLongitude, lastLatitude;

    public void update(String state, long lastTime, double lastLongitude, double lastLatitude) {
        this.state = state;
        this.lastTime = lastTime;
        this.lastLongitude = lastLongitude;
        this.lastLatitude = lastLatitude;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getState() {
        return state;
    }

    public String getConnectKey() {
        return connectKey;
    }

    public long getLastTime() {
        return lastTime;
    }

    public double getLastLongitude() {
        return lastLongitude;
    }

    public double getLastLatitude() {
        return lastLatitude;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setConnectKey(String connectKey) {
        this.connectKey = connectKey;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public void setLastLongitude(double lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public void setLastLatitude(double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }
}

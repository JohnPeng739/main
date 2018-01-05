package org.mx.comps.notify.online;

/**
 * 在线设备对象定义
 *
 * @author : john.peng created on date : 2018/1/3
 */
public class OnlineDevice {
    private String deviceId, state;
    private long lastTime, lastLongitude, lastLatitude;

    public String getDeviceId() {
        return deviceId;
    }

    public String getState() {
        return state;
    }

    public long getLastTime() {
        return lastTime;
    }

    public long getLastLongitude() {
        return lastLongitude;
    }

    public long getLastLatitude() {
        return lastLatitude;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public void setLastLongitude(long lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public void setLastLatitude(long lastLatitude) {
        this.lastLatitude = lastLatitude;
    }
}

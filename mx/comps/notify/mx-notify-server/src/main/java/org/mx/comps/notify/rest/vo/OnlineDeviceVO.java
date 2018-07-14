package org.mx.comps.notify.rest.vo;

import org.mx.comps.notify.online.OnlineDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线设备值对象定义
 *
 * @author : john.peng created on date : 2018/1/14
 */
public class OnlineDeviceVO {
    private String deviceId, state, connect, extraData;
    private long registryTime, lastTime;
    private double lastLongitude, lastLatitude;

    /**
     * 将在线设备对象转换为对应的值对象
     *
     * @param device 在线设备对象
     * @param vo     值对象
     */
    public static void transform(OnlineDevice device, OnlineDeviceVO vo) {
        if (device == null || vo == null) {
            return;
        }
        vo.deviceId = device.getDeviceId();
        vo.state = device.getState();
        vo.connect = device.getConnectKey();
        vo.registryTime = device.getRegistryTime();
        vo.lastTime = device.getLastTime();
        vo.lastLongitude = device.getLastLongitude();
        vo.lastLatitude = device.getLastLatitude();
        vo.extraData = device.getExtraData();
    }

    /**
     * 将在线设备列表转换为对应的值对象列表
     *
     * @param devices 在线设备列表
     * @return 值对象列表
     */
    public static List<OnlineDeviceVO> transform(List<OnlineDevice> devices) {
        List<OnlineDeviceVO> list = new ArrayList<>();
        if (devices != null && !devices.isEmpty()) {
            devices.forEach(device -> {
                OnlineDeviceVO vo = new OnlineDeviceVO();
                OnlineDeviceVO.transform(device, vo);
                list.add(vo);
            });
        }
        return list;
    }

    /**
     * 获取设备编号
     *
     * @return 设备编号
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 设置设备编号
     *
     * @param deviceId 设备编号
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * 获取最近状态
     *
     * @return 状态
     */
    public String getState() {
        return state;
    }

    /**
     * 设置最近一次的状态
     *
     * @param state 状态
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 获取连接串
     *
     * @return 连接串
     */
    public String getConnect() {
        return connect;
    }

    /**
     * 设置连接串
     *
     * @param connect 连接串
     */
    public void setConnect(String connect) {
        this.connect = connect;
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
     * @param registryTime 注册时间
     */
    public void setRegistryTime(long registryTime) {
        this.registryTime = registryTime;
    }

    /**
     * 获取最近一次的时间
     *
     * @return 时间
     */
    public long getLastTime() {
        return lastTime;
    }

    /**
     * 设置最近一次时间
     *
     * @param lastTime 时间
     */
    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    /**
     * 获取最近一次的经度
     *
     * @return 经度值
     */
    public double getLastLongitude() {
        return lastLongitude;
    }

    /**
     * 设置最近一次的经度
     *
     * @param lastLongitude 经度值
     */
    public void setLastLongitude(double lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    /**
     * 获取最近一次的纬度
     *
     * @return 纬度值
     */
    public double getLastLatitude() {
        return lastLatitude;
    }

    /**
     * 设置最近一次的纬度
     *
     * @param lastLatitude 纬度值
     */
    public void setLastLatitude(double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    /**
     * 获取设备扩展数据
     *
     * @return 扩展数据
     */
    public String getExtraData() {
        return extraData;
    }

    /**
     * 设置设备扩展数据
     *
     * @param extraData 扩展数据
     */
    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}

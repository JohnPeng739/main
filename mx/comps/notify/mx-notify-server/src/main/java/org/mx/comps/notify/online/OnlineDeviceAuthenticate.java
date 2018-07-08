package org.mx.comps.notify.online;

/**
 * 描述： 在线设备认证接口定义
 *
 * @author john peng
 * Date time 2018/7/8 下午2:07
 */
public interface OnlineDeviceAuthenticate {
    /**
     * 对在线设备进行身份鉴别
     *
     * @param onlineDevice 设备对象信息
     * @return 鉴别通过返回true，否则返货false
     */
    boolean authenticate(OnlineDevice onlineDevice);
}

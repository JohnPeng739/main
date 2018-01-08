package org.mx.comps.notify.processor;

import com.alibaba.fastjson.JSONObject;

import java.util.Set;

/**
 * 通知消息处理生命期监听器接口定义
 *
 * @author : john.peng created on date : 2017/1/8
 */
public interface NotifyProcessListener {
    /**
     * 通知处理前被调用
     *
     * @param data 通知数据，JSONObject对象
     */
    void before(JSONObject data);

    /**
     * 通知处理完毕后被调用
     *
     * @param data           通知数据，JSONObject对象
     * @param success        处理是否成功
     * @param invalidDevices 无效设备列表，可以缓存之后另行处理
     */
    void after(JSONObject data, boolean success, Set<String> invalidDevices);
}

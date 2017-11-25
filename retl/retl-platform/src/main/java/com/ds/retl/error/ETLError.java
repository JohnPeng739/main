package com.ds.retl.error;

import java.io.Serializable;
import java.util.Date;

/**
 * 通用的实时数据抽取、处理和加载的异常定义
 *
 * @author : john.peng created on date : 2017/9/7
 */
public class ETLError implements Serializable {
    private String type = "NA";
    private String message = "";
    private String data = "";
    private long createdtime = new Date().getTime();

    /**
     * 获取异常类型
     *
     * @return 异常类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置异常类型
     *
     * @param type 异常类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取异常消息
     *
     * @return 异常消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置异常消息
     *
     * @param message 异常消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取异常关联的数据对象
     *
     * @return 数据对象
     */
    public String getData() {
        return data;
    }

    /**
     * 设置异常关联的数据对象
     *
     * @param data 数据对象
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * 获取异常发生的时间
     *
     * @return 发生时间
     */
    public long getCreatedtime() {
        return createdtime;
    }

    /**
     * 设置异常发生的时间
     *
     * @param createdtime 发生时间
     */
    public void setCreatedtime(long createdtime) {
        this.createdtime = createdtime;
    }
}

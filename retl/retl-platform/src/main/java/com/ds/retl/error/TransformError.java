package com.ds.retl.error;

import com.alibaba.fastjson.JSONObject;

/**
 * 数据转换错误异常，在数据转换过程中发生的异常。
 *
 * @author : john.peng created on date : 2017/9/7
 */
public class TransformError extends FieldOperateError {
    /**
     * 默认的构造函数
     */
    public TransformError() {
        super();
        super.setType("transform");
    }

    /**
     * 构造函数
     *
     * @param fieldName 异常关联的字段名
     * @param message   异常消息
     */
    public TransformError(String fieldName, String message) {
        this();
        super.setFieldName(fieldName);
        super.setMessage(message);
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param data    异常关联的数据对象
     */
    public TransformError(String message, JSONObject data) {
        this();
        super.setMessage(message);
        super.setData(data.toJSONString());
    }

    /**
     * 构造函数
     *
     * @param fieldName 异常关联的字段名
     * @param message   异常消息
     * @param data      异常关联的数据对象
     */
    public TransformError(String fieldName, String message, JSONObject data) {
        this(fieldName, message);
        super.setData(data.toJSONString());
    }
}

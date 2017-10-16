package com.ds.retl.error;

import com.alibaba.fastjson.JSONObject;

/**
 * 数据校验错误异常，在数据校验过程中发生的异常。
 *
 * @author : john.peng created on date : 2017/9/9
 */
public class ValidateError extends FieldOperateError {
    /**
     * 默认的构造函数
     */
    public ValidateError() {
        super();
        super.setType("validate");
    }

    /**
     * 构造函数
     *
     * @param fieldName 异常关联的字段名
     * @param message   异常消息
     */
    public ValidateError(String fieldName, String message) {
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
    public ValidateError(String message, JSONObject data) {
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
    public ValidateError(String fieldName, String message, JSONObject data) {
        this(fieldName, message);
        super.setData(data.toJSONString());
    }

}

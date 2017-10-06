package com.ds.retl.error;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by john on 2017/9/7.
 */
public class ValidateError extends FieldOperateError {
    public ValidateError() {
        super();
        super.setType("validate");
    }

    public ValidateError(String fieldName, String message) {
        this();
        super.setFieldName(fieldName);
        super.setMessage(message);
    }

    public ValidateError(String message, JSONObject data) {
        this();
        super.setMessage(message);
        super.setData(data.toJSONString());
    }

    public ValidateError(String fieldName, String message, JSONObject data) {
        this(fieldName, message);
        super.setData(data.toJSONString());
    }

}

package com.ds.retl.error;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by john on 2017/9/7.
 */
public class TransformError extends FieldOperateError {
    public TransformError() {
        super();
        super.setType("transform");
    }

    public TransformError(String fieldName, String message) {
        this();
        super.setFieldName(fieldName);
        super.setMessage(message);
    }

    public TransformError(String message, JSONObject data) {
        this();
        super.setMessage(message);
        super.setData(data.toJSONString());
    }

    public TransformError(String fieldName, String message, JSONObject data) {
        this(fieldName, message);
        super.setData(data.toJSONString());
    }
}

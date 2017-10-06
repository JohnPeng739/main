package com.ds.retl.error;

/**
 * Created by john on 2017/9/7.
 */
public class FieldOperateError extends ETLError {
    private String fieldName = null;

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}

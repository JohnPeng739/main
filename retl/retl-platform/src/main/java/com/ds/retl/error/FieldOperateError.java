package com.ds.retl.error;

/**
 * 字段处理异常定义
 *
 * @author : john.peng created on date : 2017/9/7
 * @see ETLError
 */
public class FieldOperateError extends ETLError {
    private String fieldName = null;

    /**
     * 获取异常关联的字段名
     *
     * @return 字段名
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * 设置异常关联的字段名
     *
     * @param fieldName 字段名
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}

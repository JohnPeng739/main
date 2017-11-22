package com.ds.retl.rest.vo;

/**
 * 名称-数据元组的值对象定义
 *
 * @author : john.peng created on date : 2017/11/22
 */
public class NameValueVO {
    private String name, value;

    public NameValueVO() {
        super();
    }

    public NameValueVO(String name, String value) {
        this();
        this.name = name;
        this.value = value;
    }

    public NameValueVO(String name, int value) {
        this(name, String.valueOf(value));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}

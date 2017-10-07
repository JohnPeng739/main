package org.mx.dal.entity;

/**
 * Created by john on 2017/8/13.
 */
public interface BaseDict extends Base {
    String getCode();
    void setCode(String code);
    String getName();
    void setName(String name);
    String getDesc();
    void setDesc(String desc);
}

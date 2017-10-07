package org.mx.dal.entity;

/**
 * Created by john on 2017/8/13.
 */
public interface BaseDictTree extends BaseDict {
    String getParentId();
    void setParentId(String parentId);
}

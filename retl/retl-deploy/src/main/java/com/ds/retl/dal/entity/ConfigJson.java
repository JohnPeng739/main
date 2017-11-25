package com.ds.retl.dal.entity;

import org.mx.dal.entity.BaseDict;

/**
 * Created by john on 2017/11/2.
 */
public interface ConfigJson extends BaseDict {
    String getConfigContent();
    void setConfigContent(String configContent);
}

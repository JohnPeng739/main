package com.ds.retl.dal.entity;

import org.mx.dal.entity.Base;

/**
 * Created by john on 2017/10/8.
 */
public interface UserOperateLog extends Base {
    void setContent(String content);
    String getContent();
}

package com.ds.retl.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by john on 2017/10/8.
 */
@Entity
@Table(name = "TB_OPERATE_LOG")
public class UserOperateLogEntity extends BaseEntity implements UserOperateLog {
    @Column(nullable = false)
    private String content;

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }
}

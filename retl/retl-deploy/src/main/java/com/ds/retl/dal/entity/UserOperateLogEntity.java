package com.ds.retl.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 基于Hibernate JPA实现用户操作日志实体类
 *
 * @author : john.peng created on date : 2017/10/8
 * @see BaseEntity
 * @see UserOperateLog
 */
@Entity
@Table(name = "TB_OPERATE_LOG")
public class UserOperateLogEntity extends BaseEntity implements UserOperateLog {
    @Column(nullable = false)
    private String content;

    /**
     * {@inheritDoc}
     *
     * @see UserOperateLog#getContent()
     */
    @Override
    public String getContent() {
        return content;
    }

    /**
     * {@inheritDoc}
     *
     * @see UserOperateLog#setContent(String)
     */
    @Override
    public void setContent(String content) {
        this.content = content;
    }
}

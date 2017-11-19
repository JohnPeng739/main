package org.mx.dal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 基于Hibernate JPA实现账户操作日志实体类
 *
 * @author : john.peng created on date : 2017/10/8
 * @see BaseEntity
 * @see OperateLog
 */
@Entity
@Table(name = "TB_OPERATE_LOG")
public class OperateLogEntity extends BaseEntity implements OperateLog {
    @Column(nullable = false)
    private String content;

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#getContent()
     */
    @Override
    public String getContent() {
        return content;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#setContent(String)
     */
    @Override
    public void setContent(String content) {
        this.content = content;
    }
}

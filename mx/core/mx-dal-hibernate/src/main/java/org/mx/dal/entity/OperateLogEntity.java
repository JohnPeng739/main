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
    @Column(name = "SYSTEM", nullable = false)
    private String system;
    @Column(name = "MODULE", nullable = false)
    private String module;
    @Column(name = "OPERATE_TYPE", nullable = false)
    private OperateType operateType = OperateType.QUERY;
    @Column(name = "CONTENT", nullable = false)
    private String content;

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#getSystem()
     */
    @Override
    public String getSystem() {
        return system;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#setSystem(String)
     */
    @Override
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#getModule()
     */
    @Override
    public String getModule() {
        return module;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#setModule(String)
     */
    @Override
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#getOperateType()
     */
    @Override
    public OperateType getOperateType() {
        return operateType;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLog#setOperateType(OperateType)
     */
    @Override
    public void setOperateType(OperateType type) {
        this.operateType = type;
    }

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

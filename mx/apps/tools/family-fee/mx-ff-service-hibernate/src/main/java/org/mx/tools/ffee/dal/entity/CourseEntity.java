package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 描述： 科目明细信息实体类，基于Hibernate实现。
 *
 * @author: John.Peng
 * @date: 2018/2/17 下午5:30
 */
@Entity
@Table(name = "TB_COURSE")
public class CourseEntity extends BaseEntity implements Course {
    @Column(name = "NAME", length = 50)
    private String name;
    @Column(name = "DESCRIPTION")
    private String desc;
    @Column(name = "COURSE_TYPE")
    private BudgetItem.Type type;

    /**
     * {@inheritDoc}
     *
     * @see Course#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @see Course#setName(String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     *
     * @see Course#getDesc()
     */
    public String getDesc() {
        return desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see Course#setDesc(String)
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see Course#getType()
     */
    public BudgetItem.Type getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     *
     * @see Course#setType(BudgetItem.Type)
     */
    public void setType(BudgetItem.Type type) {
        this.type = type;
    }
}

package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;

/**
 * 描述： 科目明细信息实体类，基于Hibernate实现。
 *
 * @author: John.Peng
 * @date: 2018/2/17 下午5:30
 */
public class CourceEntity extends BaseEntity implements Cource {
    @Column(name = "NAME", length = 50)
    private String name;
    @Column(name = "DESCRIPTION", length = 255)
    private String desc;
    @Column(name = "COURCE_TYPE")
    private BudgetItem.Type type;

    /**
     * {@inheritDoc}
     *
     * @see Cource#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @see Cource#setName(String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     *
     * @see Cource#getDesc()
     */
    public String getDesc() {
        return desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see Cource#setDesc(String)
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see Cource#getType()
     */
    public BudgetItem.Type getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     *
     * @see Cource#setType(BudgetItem.Type)
     */
    public void setType(BudgetItem.Type type) {
        this.type = type;
    }
}

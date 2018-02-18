package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 描述： 预算明细信息实体类，基于hibernate实现。
 *
 * @author: John.Peng
 * @date: 2018/2/18 上午10:22
 */
@Entity
@Table(name = "TB_BUDGET_ITEM")
public class BudgetItemEntity extends BaseEntity implements BudgetItem {
    @Column(name = "BUDGET_YEAR")
    private int year;
    @Column(name = "BUDGET_TYPE")
    private Type type;
    @Column(name = "MONEY")
    private double money;
    @Column(name = "DESCRIPTION")
    private String desc;
    @ManyToOne(targetEntity = CourseEntity.class)
    private Course course;

    /**
     * {@inheritDoc}
     *
     * @see BudgetItem#getYear()
     */
    public int getYear() {
        return year;
    }

    /**
     * {@inheritDoc}
     *
     * @see BudgetItem#setYear(int)
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * {@inheritDoc}
     *
     * @see BudgetItem#getType()
     */
    public Type getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     *
     * @see BudgetItem#setType(Type)
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     *
     * @see BudgetItem#getMoney()
     */
    public double getMoney() {
        return money;
    }

    /**
     * {@inheritDoc}
     *
     * @see BudgetItem#setMoney(double)
     */
    public void setMoney(double money) {
        this.money = money;
    }

    /**
     * {@inheritDoc}
     *
     * @see BudgetItem#getDesc()
     */
    public String getDesc() {
        return desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see BudgetItem#setDesc(String)
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see BudgetItem#getCource()
     */
    public Course getCource() {
        return course;
    }

    /**
     * {@inheritDoc}
     *
     * @see BudgetItem#setCource(Course)
     */
    public void setCource(Course course) {
        this.course = course;
    }
}

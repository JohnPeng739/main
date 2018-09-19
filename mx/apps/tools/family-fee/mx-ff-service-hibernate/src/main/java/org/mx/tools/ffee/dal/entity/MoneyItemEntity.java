package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * 描述： 收入或支持的明细信息实体超类，基于Hibernate实现。
 *
 * @author John.Peng
 * Date time 2018/2/18 上午11:02
 */
@MappedSuperclass
public class MoneyItemEntity extends BaseEntity implements MoneyItem {
    @ManyToOne(targetEntity = FamilyEntity.class)
    @JoinColumn(name = "FAMILY_ID")
    private Family family;
    @ManyToOne(targetEntity = CourseEntity.class)
    @JoinColumn(name = "COURSE_ID")
    private Course course;
    @Column(name = "MONEY")
    private double money;
    @Column(name = "OCCUR_TIME")
    private long occurTime = System.currentTimeMillis();
    @Column(name = "DESCRIPTION", length = 300)
    private String desc;
    @ManyToOne(targetEntity = FfeeAccountEntity.class)
    @JoinColumn(name = "ACCOUNT_ID")
    private FfeeAccount owner;

    /**
     * {@inheritDoc}
     *
     * @see MoneyItem#getFamily()
     */
    @Override
    public Family getFamily() {
        return family;
    }

    /**
     * {@inheritDoc}
     *
     * @see MoneyItem#setFamily(Family)
     */
    @Override
    public void setFamily(Family family) {
        this.family = family;
    }

    /**
     * {@inheritDoc}
     *
     * @see MoneyItem#getCourse()
     */
    public Course getCourse() {
        return course;
    }

    /**
     * {@inheritDoc}
     *
     * @see MoneyItem#setCourse(Course)
     */
    public void setCourse(Course course) {
        this.course = course;
    }

    /**
     * {@inheritDoc}
     *
     * @see MoneyItem#getMoney()
     */
    public double getMoney() {
        return money;
    }

    /**
     * {@inheritDoc}
     *
     * @see MoneyItem#setMoney(double)
     */
    public void setMoney(double money) {
        this.money = money;
    }

    /**
     * {@inheritDoc}
     *
     * @see MoneyItem#getOccurTime()
     */
    @Override
    public long getOccurTime() {
        return occurTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see MoneyItem#setOccurTime(long)
     */
    @Override
    public void setOccurTime(long time) {
        this.occurTime = time;
    }

    /**
     * {@inheritDoc}
     *
     * @see MoneyItem#getDesc()
     */
    public String getDesc() {
        return desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see MoneyItem#setDesc(String)
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * {@inheritDoc}
     *
     * @see MoneyItem#getOwner()
     */
    public FfeeAccount getOwner() {
        return owner;
    }

    /**
     * {@inheritDoc}
     *
     * @see MoneyItem#setOwner(FfeeAccount)
     */
    public void setOwner(FfeeAccount owner) {
        this.owner = owner;
    }
}

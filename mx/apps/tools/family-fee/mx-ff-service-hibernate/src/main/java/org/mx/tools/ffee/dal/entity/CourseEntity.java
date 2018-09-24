package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseDictTreeEntity;

import javax.persistence.*;

/**
 * 描述： 科目明细信息实体类，基于Hibernate实现。
 *
 * @author John.Peng
 * Date time 2018/2/17 下午5:30
 */
@Entity
@Table(name = "TB_COURSE")
public class CourseEntity extends BaseDictTreeEntity<CourseEntity> implements Course {
    @Column(name = "COURSE_TYPE", length = 30)
    private CourseType type = CourseType.INCOME;
    @ManyToOne(targetEntity = FfeeAccountEntity.class)
    @JoinColumn(name = "FAMILY_ID")
    private Family owner;
    @Column(name = "COURSE_ORDER")
    private float order;

    /**
     * {@inheritDoc}
     *
     * @see Course#getType()
     */
    public CourseType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     *
     * @see Course#setType(CourseType)
     */
    public void setType(CourseType type) {
        this.type = type;
    }

    @Override
    public Family getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Family owner) {
        this.owner = owner;
    }

    @Override
    public float getOrder() {
        return order;
    }

    @Override
    public void setOrder(float order) {
        this.order = order;
    }
}

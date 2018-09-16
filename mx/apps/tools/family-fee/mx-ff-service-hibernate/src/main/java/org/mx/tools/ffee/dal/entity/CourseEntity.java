package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseDictTreeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 描述： 科目明细信息实体类，基于Hibernate实现。
 *
 * @author John.Peng
 * Date time 2018/2/17 下午5:30
 */
@Entity
@Table(name = "TB_COURSE")
public class CourseEntity extends BaseDictTreeEntity implements Course {
    @Column(name = "COURSE_TYPE")
    private CourseType type = CourseType.INCOME;
    @ManyToOne(targetEntity = FfeeAccountEntity.class)
    private FfeeAccount owner;

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
    public FfeeAccount getOwner() {
        return owner;
    }

    @Override
    public void setOwner(FfeeAccount owner) {
        this.owner = owner;
    }
}

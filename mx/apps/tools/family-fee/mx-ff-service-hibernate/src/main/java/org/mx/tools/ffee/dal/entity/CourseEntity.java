package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.BaseDictTreeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 描述： 科目明细信息实体类，基于Hibernate实现。
 *
 * @author John.Peng
 *         Date time 2018/2/17 下午5:30
 */
@Entity
@Table(name = "TB_COURSE")
public class CourseEntity extends BaseDictTreeEntity implements Course {
    @Column(name = "COURSE_TYPE")
    private BudgetItem.Type type;
    @Column(name = "IS_PUBLIC")
    private boolean isPublic;

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

    /**
     * {@inheritDoc}
     *
     * @see Course#isPublic()
     */
    public boolean isPublic() {
        return isPublic;
    }

    /**
     * {@inheritDoc}
     *
     * @see Course#setPublic(boolean)
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}

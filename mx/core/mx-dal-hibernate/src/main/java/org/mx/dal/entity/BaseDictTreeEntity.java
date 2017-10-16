package org.mx.dal.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 基于Hibernate实现的基础树状字典实体
 *
 * @author : john.peng date : 2017/10/6
 * @see BaseDictEntity
 * @see BaseDictTree
 */
@MappedSuperclass
public class BaseDictTreeEntity extends BaseDictEntity implements BaseDictTree {
    @Column(name = "PARENT_ID", length = 40)
    private String parentId;

    /**
     * {@inheritDoc}
     * @see BaseDictEntity#toString()
     */
    @Override
    public String toString() {
        return super.toString() +
                ", parentId='" + parentId + '\'';
    }

    /**
     * {@inheritDoc}
     * @see BaseDictTree#getParentId()
     */
    @Override
    public String getParentId() {
        return parentId;
    }

    /**
     * {@inheritDoc}
     * @see BaseDictTree#setParentId(String)
     */
    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}

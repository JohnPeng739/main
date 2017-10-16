package org.mx.dal.entity;

/**
 * Created by john on 2017/10/8.
 */

/**
 * 基于Mongodb实现的基础树状字典实体
 *
 * @author : john.peng date : 2017/10/8
 * @see BaseDictEntity
 * @see BaseDictTree
 */
public class BaseDictTreeEntity extends BaseDictEntity implements BaseDictTree {
    private String parentId;

    /**
     * {@inheritDoc}
     *
     * @see BaseDictEntity#toString()
     */
    @Override
    public String toString() {
        return super.toString() +
                ", parentId='" + parentId + '\'';
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseDictTree#getParentId()
     */
    @Override
    public String getParentId() {
        return parentId;
    }

    /**
     * {@inheritDoc}
     *
     * @see BaseDictTree#setParentId(String)
     */
    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}

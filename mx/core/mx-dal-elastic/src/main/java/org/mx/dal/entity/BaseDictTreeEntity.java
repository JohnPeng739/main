package org.mx.dal.entity;

/**
 * 描述： 基于Elastic实现的基础树状字典实体
 *
 * @author John.Peng
 *         Date time 2018/4/1 上午8:48
 */
public class BaseDictTreeEntity extends BaseDictEntity implements BaseDictTree {
    private String parentId;

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

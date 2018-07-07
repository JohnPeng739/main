package org.mx.dal.entity;

import org.mx.dal.annotation.ElasticField;

/**
 * 描述： 基于Elasticsearch实现的基础类实体
 *
 * @author John.Peng
 * Date time 2018/4/1 上午8:41
 */
public class ElasticBaseEntity implements Base {
    @ElasticField
    private String id, operator;
    @ElasticField(type = "long")
    private long createdTime, updatedTime;
    @ElasticField(type = "boolean")
    private boolean valid = true;

    private float score = 0.0f;

    /**
     * {@inheritDoc}
     *
     * @see Base#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @see Base#setId(String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     *
     * @see Base#getOperator()
     */
    @Override
    public String getOperator() {
        return operator;
    }

    /**
     * {@inheritDoc}
     *
     * @see Base#setOperator(String)
     */
    @Override
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * {@inheritDoc}
     *
     * @see Base#getCreatedTime()
     */
    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see Base#setCreatedTime(long)
     */
    @Override
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see Base#getUpdatedTime()
     */
    @Override
    public long getUpdatedTime() {
        return updatedTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see Base#setUpdatedTime(long)
     */
    @Override
    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * {@inheritDoc}
     *
     * @see Base#isValid()
     */
    @Override
    public boolean isValid() {
        return valid;
    }

    /**
     * {@inheritDoc}
     *
     * @see Base#setValid(boolean)
     */
    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * 获取Elastic的匹配分数
     *
     * @return 分数
     */
    public float getScore() {
        return score;
    }

    /**
     * 设置Elastic的匹配分数
     *
     * @param score 分数
     */
    public void setScore(float score) {
        this.score = score;
    }
}

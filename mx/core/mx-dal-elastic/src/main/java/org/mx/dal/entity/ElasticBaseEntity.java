package org.mx.dal.entity;

import org.mx.dal.annotation.ElasticField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述： 基于Elasticsearch实现的基础类实体
 *
 * @author John.Peng
 * Date time 2018/4/1 上午8:41
 */
public class ElasticBaseEntity implements Base {
    @ElasticField
    private String id, operator; // ID、操作者
    @ElasticField(type = "long")
    private long createdTime, updatedTime; // 创建时间、更新时间
    @ElasticField(type = "boolean")
    private boolean valid = true; // 是否有效

    // 存储搜索结果的命中分数
    private float score = 0.0f;
    // 存储搜索结果中可能存在的命中高亮字段（仅针对CONTAIN搜索条件字段）
    private Map<String, List<String>> highlights = new HashMap<>();

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

    /**
     * 获取高亮显示字段及其内容
     *
     * @return 高亮显示字段及其内容集合
     */
    public Map<String, List<String>> getHighlights() {
        return highlights;
    }
}

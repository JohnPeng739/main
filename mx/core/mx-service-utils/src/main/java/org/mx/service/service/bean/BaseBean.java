package org.mx.service.service.bean;

import org.mx.dal.entity.Base;

/**
 * 描述： 基础信息对象类
 *
 * @author john peng
 * Date time 2018/5/6 下午3:00
 */
public class BaseBean implements Base {
    private String id, operator;
    private long createdTime, updatedTime;
    private boolean valid = true;

    /**
     * {@inheritDoc}
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                ", operator='" + operator + '\'' +
                ", valid=" + valid;
    }

    /**
     * {@inheritDoc}
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseBean that = (BaseBean) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    /**
     * {@inheritDoc}
     *
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

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
}

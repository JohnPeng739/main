package org.mx.dal.entity;

import org.mx.StringUtils;
import org.springframework.data.annotation.Id;

/**
 * 基于Mongodb实现的基础实体
 *
 * @author : john.peng date : 2017/10/8
 * @see Base
 */
public class BaseEntity implements Base {
    @Id
    private String id;

    private long createdTime, updatedTime;
    private String operator;
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

        BaseEntity that = (BaseEntity) o;
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        return result;
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

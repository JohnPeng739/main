package org.mx.dal.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 基于Hibernate实现的基础实体
 *
 * @author : john.peng date : 2017/10/6
 * @see Base
 */
@MappedSuperclass
public class BaseEntity implements Base {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "ID", nullable = false, length = 40)
    private String id;

    @Column(name = "CREATED_TIME", nullable = false)
    private long createdTime;
    @Column(name = "UPDATED_TIME", nullable = false)
    private long updatedTime;
    @Column(name = "OPERATOR", nullable = false, length = 40)
    private String operator;
    @Column(name = "VALID", nullable = false)
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

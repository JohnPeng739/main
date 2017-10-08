package org.mx.dal.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Created by john on 2017/10/6.
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

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                ", operator='" + operator + '\'' +
                ", valid=" + valid;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public long getUpdatedTime() {
        return updatedTime;
    }

    @Override
    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}

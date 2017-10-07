package org.mx.dal.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by john on 2017/10/6.
 */
@Entity
public class BaseEntity implements Base {
    @Id
    private String id;

    private long createdTime, updatedTime;
    private String operator;
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
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public long getUpdatedTime() {
        return updatedTime;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public boolean isValid() {
        return valid;
    }
}

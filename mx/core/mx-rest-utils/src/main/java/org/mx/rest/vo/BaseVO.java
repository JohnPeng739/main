package org.mx.rest.vo;

import org.mx.dal.entity.Base;

/**
 * Created by john on 2017/10/8.
 */
public class BaseVO {
    private String id, operator;
    private long createdTime, updatedTime;
    private boolean valid;

    public static void transform(BaseVO baseVO, Base base) {
        if (baseVO == null || base == null) {
            return;
        }
        base.setId(baseVO.getId());
        base.setOperator(baseVO.getOperator());
        base.setCreatedTime(baseVO.getCreatedTime());
        base.setUpdatedTime(baseVO.getUpdatedTime());
        base.setValid(baseVO.isValid());
    }

    public static void transform(Base base, BaseVO baseVO) {
        if (base == null || baseVO == null) {
            return;
        }
        baseVO.id = base.getId();
        baseVO.operator = base.getOperator();
        baseVO.createdTime = base.getCreatedTime();
        baseVO.updatedTime = base.getUpdatedTime();
        baseVO.valid = base.isValid();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getId() {
        return id;
    }

    public String getOperator() {
        return operator;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public boolean isValid() {
        return valid;
    }
}

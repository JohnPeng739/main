package org.mx.dal.entity;

/**
 * Created by john on 2017/8/13.
 */
public interface Base {
    String getId();
    void setId(String id);
    long getCreatedTime();
    void setCreatedTime(long createdTime);
    long getUpdatedTime();
    void setUpdatedTime(long updatedTime);
    boolean isValid();
    void setValid(boolean valid);
    String getOperator();
    void setOperator(String operator);
}

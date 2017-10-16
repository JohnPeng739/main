package org.mx.dal.entity;

/**
 * 基础实体接口定义
 *
 * @author : john.peng date : 2017/8/13
 */
public interface Base {
    /**
     * 获取关键字ID
     *
     * @return 关键字
     */
    String getId();

    /**
     * 设置关键字ID
     *
     * @param id 关键字
     */
    void setId(String id);

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    long getCreatedTime();

    /**
     * 设置创建时间
     *
     * @param createdTime 创建时间
     */
    void setCreatedTime(long createdTime);

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    long getUpdatedTime();

    /**
     * 设置更新时间
     *
     * @param updatedTime 更新时间
     */
    void setUpdatedTime(long updatedTime);

    /**
     * 获取是否有效
     *
     * @return 是否有效
     */
    boolean isValid();

    /**
     * 设置是否有效
     *
     * @param valid 是否有效
     */
    void setValid(boolean valid);

    /**
     * 获取操作者代码
     *
     * @return 操作者代码
     */
    String getOperator();

    /**
     * 设置操作者代码
     *
     * @param operator 操作者代码
     */
    void setOperator(String operator);
}

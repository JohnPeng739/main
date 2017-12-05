package org.mx.service.rest.vo;

import org.mx.dal.entity.Base;

/**
 * 基础值对象
 *
 * @author : john.peng date : 2017/10/8
 */
public class BaseVO {
    private String id, operator;
    private long createdTime, updatedTime;
    private boolean valid;

    /**
     * 将值对象转换为实体对象
     *
     * @param baseVO 值对象
     * @param base   实体对象
     */
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

    /**
     * 将实体对象转换为值对象
     *
     * @param base   实体对象
     * @param baseVO 值对象
     */
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

    /**
     * 获取关键字ID
     *
     * @return 关键字
     */
    public String getId() {
        return id;
    }

    /**
     * 设置关键字ID
     *
     * @param id 关键字
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取操作者代码
     *
     * @return 操作者代码
     */
    public String getOperator() {
        return operator;
    }

    /**
     * 设置操作者代码
     *
     * @param operator 操作者代码
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public long getCreatedTime() {
        return createdTime;
    }

    /**
     * 设置创建时间
     *
     * @param createdTime 创建时间
     */
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    public long getUpdatedTime() {
        return updatedTime;
    }

    /**
     * 设置更新时间
     *
     * @param updatedTime 更新时间
     */
    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * 获取是否有效
     *
     * @return 是否有效
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * 设置是否有效
     *
     * @param valid 是否有效
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}

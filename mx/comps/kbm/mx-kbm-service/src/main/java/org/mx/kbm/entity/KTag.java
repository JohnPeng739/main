package org.mx.kbm.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： 知识标签接口定义，定义了知识标签的描述性信息。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午6:56
 */
public interface KTag extends Base {
    /**
     * 获取标签
     *
     * @return 标签
     */
    String getTag();

    /**
     * 设置标签
     *
     * @param tag 标签
     */
    void setTag(String tag);

    /**
     * 获取标签描述
     *
     * @return 描述
     */
    String getDesc();

    /**
     * 设置标签描述
     *
     * @param desc 描述
     */
    void setDesc(String desc);

    /**
     * 获取租户
     *
     * @return 租户
     */
    Tenant getTenant();

    /**
     * 设置租户
     *
     * @param tenant 租户
     */
    void setTenant(Tenant tenant);
}

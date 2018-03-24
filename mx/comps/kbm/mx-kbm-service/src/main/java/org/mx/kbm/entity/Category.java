package org.mx.kbm.entity;

import org.mx.dal.entity.BaseDictTree;

/**
 * 描述： 知识分类接口定义，定义知识分类目录，树状结构。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午6:13
 */
public interface Category extends BaseDictTree {
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

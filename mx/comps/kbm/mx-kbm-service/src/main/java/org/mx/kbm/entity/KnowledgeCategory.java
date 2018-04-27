package org.mx.kbm.entity;

import org.mx.dal.entity.BaseDictTree;

/**
 * 描述： 知识分类接口定义，定义知识分类目录，树状结构。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午6:13
 */
public interface KnowledgeCategory extends BaseDictTree {
    /**
     * 获取租户
     *
     * @return 租户
     */
    KnowledgeTenant getTenant();

    /**
     * 设置租户
     *
     * @param knowledgeTenant 租户
     */
    void setTenant(KnowledgeTenant knowledgeTenant);

    /**
     * 获取是否私有
     *
     * @return 返回true表示私有，否则返回true
     */
    boolean isPriviated();

    /**
     * 设置是否私有
     *
     * @param privated 设置为true表示私有，设置为false表示公开
     */
    void setPrivated(boolean privated);
}

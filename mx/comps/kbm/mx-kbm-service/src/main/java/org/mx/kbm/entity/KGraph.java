package org.mx.kbm.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： 知识图谱接口定义，定义了知识和知识之间的关联关系。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午6:59
 */
public interface KGraph extends Base {
    /**
     * 获取源知识条目
     *
     * @return 源
     */
    KNode getSrc();

    /**
     * 设置源知识条目
     *
     * @param src 源
     */
    void setSrc(KNode src);

    /**
     * 获取目标知识条目
     *
     * @return 目标
     */
    KNode getTar();

    /**
     * 设置目标知识条目
     *
     * @param tar 目标
     */
    void setTar(KNode tar);

    /**
     * 获取知识之间的关系
     *
     * @return 关系
     * @see NodeRelationship
     */
    NodeRelationship getRelationship();

    /**
     * 设置知识之间的关系
     *
     * @param relationship 关系
     * @see NodeRelationship
     */
    void setRelationship(NodeRelationship relationship);

    /**
     * 知识条件之间的关系枚举定义
     */
    enum NodeRelationship {
        /**
         * 引用
         */
        REFERENCE,
        /**
         * 包含
         */
        INCLUDE,
        /**
         * 拓展
         */
        EXTEND
    }
}

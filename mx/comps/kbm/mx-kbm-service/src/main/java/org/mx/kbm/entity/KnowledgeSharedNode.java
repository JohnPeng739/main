package org.mx.kbm.entity;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.dal.entity.Base;

/**
 * 描述： 分享的知识条目接口定义
 *
 * @author John.Peng
 *         Date time 2018/4/27 下午4:20
 */
public interface KnowledgeSharedNode extends Base {
    /**
     * 获取知识条目对象，分享源
     *
     * @return 知识条目对象
     */
    KnowledgeNode getNode();

    /**
     * 设置知识条目对象，分享源
     *
     * @param node 知识条目对象
     */
    void setNode(KnowledgeNode node);

    /**
     * 获取访问控制状态，值可以是枚举KAcl的组合。
     *
     * @return 访问控制状态
     */
    int getAcls();

    /**
     * 设置访问控制状态，值可以是枚举KAcl的组合。
     *
     * @param acls 访问控制状态
     */
    void setAcls(int acls);

    /**
     * 设置当前知识条目的访问控制为：读 + 写
     */
    void setAclsReadWrite();

    /**
     * 设置当前知识条目的访问控制为：读 + 写 + 删除
     */
    void setAclsReadWriteDelete();

    /**
     * 设置当前知识条目的访问控制为：读 + 写 + 分享
     */
    void setAclsReadWriteShare();

    /**
     * 获取分享受益人账户
     *
     * @return 分享受益人账户
     */
    Account getBeneficiary();

    /**
     * 设置分享受益人账户
     *
     * @param beneficiary 分享受益人账户
     */
    void setBeneficiary(Account beneficiary);

    /**
     * 获取分享开始时间
     *
     * @return 分享开始时间
     */
    long getStartTime();

    /**
     * 设置分享开始时间
     *
     * @param startTime 分享开始时间
     */
    void setStartTime(long startTime);

    /**
     * 获取分享结束时间
     *
     * @return 分享结束时间
     */
    long getEndTime();

    /**
     * 设置分享结束时间
     *
     * @param endTime 分享结束时间
     */
    void setEndTime(long endTime);

    /**
     * 获取分享状态
     *
     * @return 分享状态
     * @see SharedState
     */
    SharedState getState();

    /**
     * 设置分享状态
     *
     * @param state 分享状态
     * @see SharedState
     */
    void setState(SharedState state);

    /**
     * 知识共享状态枚举
     */
    enum SharedState {
        /**
         * 正常分享状态
         */
        SHARED,
        /**
         * 分享收回状态
         */
        RECYCLED
    }
}

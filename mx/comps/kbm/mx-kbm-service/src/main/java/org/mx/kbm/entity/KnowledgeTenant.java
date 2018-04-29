package org.mx.kbm.entity;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.dal.entity.BaseDict;

import java.util.Set;

/**
 * 描述： 知识租户接口定义，租户允许建立租户树，定义了知识租户的描述性信息，一般私有知识只能从属于一个租户。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午6:15
 */
public interface KnowledgeTenant extends BaseDict {
    /**
     * 获取租户成员
     *
     * @return 账户列表
     */
    Set<KnowledgeContact> getMembers();

    /**
     * 设置租户成员
     *
     * @param members 账户列表
     */
    void setMembers(Set<KnowledgeContact> members);

    /**
     * 获取租户类型
     *
     * @return 类型
     * @see TenantType
     */
    TenantType getType();

    /**
     * 设置租户类型
     *
     * @param tenantType 类型
     * @see TenantType
     */
    void setType(TenantType tenantType);

    /**
     * 获取租户管理员账户
     *
     * @return 管理员账户
     */
    KnowledgeContact getContact();

    /**
     * 设置租户管理员账户
     *
     * @param contact 管理员账户
     */
    void setContact(KnowledgeContact contact);

    enum TenantType {
        /**
         * 组织租户
         */
        ORGANIZATION,
        /**
         * 个人租户
         */
        PERSONAL,
        /**
         * 其他类型租户
         */
        OTHERS
    }
}

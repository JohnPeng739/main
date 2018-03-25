package org.mx.kbm.entity;

import org.mx.dal.entity.BaseDictTree;

/**
 * 描述： 知识租户接口定义，租户允许建立租户树，定义了知识租户的描述性信息，一般私有知识只能从属于一个租户。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午6:15
 */
public interface Tenant extends BaseDictTree {
    /**
     * 获取租户类型
     *
     * @return 类型
     * @see TenantType
     */
    TenantType getTenantType();

    /**
     * 设置租户类型
     *
     * @param tenantType 类型
     * @see TenantType
     */
    void setTenantType(TenantType tenantType);

    /**
     * 获取租户管理员名称
     *
     * @return 名称
     */
    String getManagerName();

    /**
     * 设置租户管理员名称
     *
     * @param managerName 名称
     */
    void setManagerName(String managerName);

    /**
     * 获取租户管理员电话
     *
     * @return 电话
     */
    String getManagerPhone();

    /**
     * 设置租户管理员电话
     *
     * @param managerPhone 电话
     */
    void setManagerPhone(String managerPhone);

    /**
     * 获取租户管理员EMail
     *
     * @return EMail
     */
    String getManagerEmail();

    /**
     * 设置租户管理员EMail
     *
     * @param managerEmail EMail
     */
    void setManagerEmail(String managerEmail);

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

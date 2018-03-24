package org.mx.kbm.entity;

import org.mx.dal.entity.Base;

import java.util.Set;

/**
 * 描述： 知识条目接口定义，定义了知识的描述信息信息，一个知识点就是一条记录。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午6:14
 */
public interface KNode extends Base {
    /**
     * 获取访问控制状态，值可以是枚举KAcl的组合。
     *
     * @return 访问控制状态
     */
    int getAcl();

    /**
     * 设置访问控制状态，值可以是枚举KAcl的组合。
     *
     * @param acl 访问控制状态
     */
    void setAcl(int acl);

    /**
     * 获取摘要
     *
     * @return 摘要
     */
    String getSummary();

    /**
     * 设置摘要
     *
     * @param summary 摘要
     */
    void setSummary(String summary);

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

    /**
     * 获取标签集合
     *
     * @return 标签集合
     */
    Set<KTag> getTags();

    /**
     * 设置标签集合
     *
     * @param tags 标签集合
     */
    void setTags(Set<KTag> tags);

    /**
     * 设置当前知识条目的访问控制为：读 + 写
     */
    void setAclReadWrite();

    /**
     * 设置当前知识条目的访问控制为：读 + 写 + 删除
     */
    void setAclReadWriteDelete();

    /**
     * 设置当前知识条目的访问控制为：读 + 写 + 分享
     */
    void setAclReadWriteShare();
}

package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.Base;

import java.util.Set;

/**
 * 描述： FFEE中家庭信息对象定义
 *
 * @author John.Peng
 *         Date time 2018/2/17 下午4:35
 */
public interface Family extends Base {
    /**
     * 获取家庭名称
     *
     * @return 家庭名称
     */
    String getName();

    /**
     * 设置家庭名称
     *
     * @param name 家庭名称
     */
    void setName(String name);

    String getAvatarUrl();

    void setAvatarUrl(String avatarUrl);

    String getDescription();

    void setDescription(String description);

    /**
     * 获取家庭成员集合
     *
     * @return 成员集合
     */
    Set<FamilyMember> getMembers();

    /**
     * 设置家庭成员集合
     *
     * @param members 成员集合
     */
    void setMembers(Set<FamilyMember> members);
}

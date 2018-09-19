package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： 家庭成员信息定义接口
 *
 * @author John.Peng
 * Date time 2018/2/18 下午7:47
 */
public interface FamilyMember extends Base {
    String getRole();

    void setRole(String role);

    FfeeAccount getFfeeAccount();

    void setFfeeAccount(FfeeAccount ffeeAccount);

    boolean isOwner();

    void setIsOwner(boolean isOwner);
}

package org.mx.tools.ffee.dal.entity;

import org.mx.dal.entity.Base;

/**
 * 描述： 家庭成员信息定义接口
 *
 * @author: John.Peng
 * @date: 2018/2/18 下午7:47
 */
public interface FamilyMember extends Base {
    String getMemberRole();
    void setMemberRole(String memberRole);
    FfeeAccount getFfeeAccount();
    void setFfeeAccount(FfeeAccount ffeeAccount);
}

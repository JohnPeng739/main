package org.mx.tools.ffee.rest.vo;

/**
 * 描述： 家庭管理信息值对象
 *
 * @author John.Peng
 *         Date time 2018/2/18 下午8:09
 */
public class FamilyManageVO {
    private String familyId, ffeeAccountId, name, memberRole;

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getFfeeAccountId() {
        return ffeeAccountId;
    }

    public void setFfeeAccountId(String ffeeAccountId) {
        this.ffeeAccountId = ffeeAccountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }
}

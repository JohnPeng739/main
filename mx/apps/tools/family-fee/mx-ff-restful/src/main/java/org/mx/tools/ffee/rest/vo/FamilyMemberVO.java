package org.mx.tools.ffee.rest.vo;

import org.mx.service.rest.vo.BaseVO;
import org.mx.tools.ffee.dal.entity.FamilyMember;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 描述： 家庭成员信息值对象
 *
 * @author John.Peng
 *         Date time 2018/2/18 下午7:54
 */
public class FamilyMemberVO extends BaseVO {
    private String memberRole;
    private FfeeAccountVO ffeeAccount;

    public static final FamilyMemberVO transform(FamilyMember familyMember) {
        if (familyMember == null) {
            return null;
        }
        FamilyMemberVO vo = new FamilyMemberVO();
        BaseVO.transform(familyMember, vo);
        vo.memberRole = familyMember.getMemberRole();
        vo.ffeeAccount = FfeeAccountVO.transform(familyMember.getFfeeAccount());
        return vo;
    }

    public static final List<FamilyMemberVO> transform(Collection<FamilyMember> familyMembers) {
        if (familyMembers == null) {
            return null;
        }
        List<FamilyMemberVO> list = new ArrayList<>();
        familyMembers.forEach(familyMember -> {
            FamilyMemberVO vo = FamilyMemberVO.transform(familyMember);
            if (vo != null) {
                list.add(vo);
            }
        });
        return list;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    public FfeeAccountVO getFfeeAccount() {
        return ffeeAccount;
    }

    public void setFfeeAccount(FfeeAccountVO ffeeAccount) {
        this.ffeeAccount = ffeeAccount;
    }
}

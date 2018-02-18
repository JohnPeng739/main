package org.mx.tools.ffee.rest.vo;

import org.mx.service.rest.vo.BaseVO;
import org.mx.tools.ffee.dal.entity.Family;

import java.util.List;

/**
 * 描述： 家庭信息值对象
 *
 * @author: John.Peng
 * @date: 2018/2/18 下午7:44
 */
public class FamilyVO extends BaseVO {
    private String name;
    private FfeeAccountVO owner;
    private List<FamilyMemberVO> members;

    public static FamilyVO transform(Family family) {
        if (family == null) {
            return null;
        }
        FamilyVO vo = new FamilyVO();
        BaseVO.transform(family, vo);
        vo.name = family.getName();
        vo.owner = FfeeAccountVO.transform(family.getOwner());
        vo.members = FamilyMemberVO.transform(family.getMembers());
        return vo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FfeeAccountVO getOwner() {
        return owner;
    }

    public void setOwner(FfeeAccountVO owner) {
        this.owner = owner;
    }

    public List<FamilyMemberVO> getMembers() {
        return members;
    }

    public void setMembers(List<FamilyMemberVO> members) {
        this.members = members;
    }
}

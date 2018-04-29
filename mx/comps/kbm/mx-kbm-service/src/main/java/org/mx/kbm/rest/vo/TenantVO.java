package org.mx.kbm.rest.vo;

import org.mx.kbm.entity.KnowledgeTenant;
import org.mx.service.rest.vo.BaseDictVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 知识租户信息值对象定义
 *
 * @author John.Peng
 *         Date time 2018/4/29 下午8:48
 */
public class TenantVO extends BaseDictVO {
    private KnowledgeTenant.TenantType type;
    private ContactVO contact;
    private List<ContactVO> members;

    public static TenantVO transform(KnowledgeTenant tenant) {
        if (tenant == null) {
            return null;
        }
        TenantVO tenantVO = new TenantVO();
        BaseDictVO.transform(tenant, tenantVO);
        tenantVO.setType(tenant.getType());
        if (tenant.getContact() != null) {
            tenantVO.setContact(ContactVO.transform(tenant.getContact()));
        }
        if (tenant.getMembers() != null && !tenant.getMembers().isEmpty()) {
            List<ContactVO> members = new ArrayList<>(tenant.getMembers().size());
            tenant.getMembers().forEach(member -> {
                if (member != null) {
                    members.add(ContactVO.transform(member));
                }
            });
            tenantVO.setMembers(members);
        }
        return tenantVO;
    }

    public KnowledgeTenant.TenantType getType() {
        return type;
    }

    public ContactVO getContact() {
        return contact;
    }

    public List<ContactVO> getMembers() {
        return members;
    }

    public void setType(KnowledgeTenant.TenantType type) {
        this.type = type;
    }

    public void setContact(ContactVO contact) {
        this.contact = contact;
    }

    public void setMembers(List<ContactVO> members) {
        this.members = members;
    }
}

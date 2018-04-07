package org.mx.kbm.rest.vo;

import org.mx.dal.EntityFactory;
import org.mx.kbm.entity.Tenant;
import org.mx.service.rest.vo.BaseDictTreeVO;

/**
 * 描述： 租户VO对象定义
 *
 * @author John.Peng
 *         Date time 2018/3/26 下午2:12
 */
public class TenantVO extends BaseDictTreeVO {
    private Tenant.TenantType tenantType = Tenant.TenantType.PERSONAL;
    private String managerName = null, managerPhone = null, managerEmail = null;

    public static TenantVO transform(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        TenantVO vo = new TenantVO();
        BaseDictTreeVO.transform(tenant, vo);
        vo.setTenantType(tenant.getTenantType());
        vo.setManagerName(tenant.getManagerName());
        vo.setManagerPhone(tenant.getManagerPhone());
        vo.setManagerEmail(tenant.getManagerEmail());
        return vo;
    }

    public Tenant transform() {
        Tenant tenant = EntityFactory.createEntity(Tenant.class);
        BaseDictTreeVO.transform(this, tenant);
        tenant.setTenantType(this.tenantType);
        tenant.setManagerName(this.managerName);
        tenant.setManagerPhone(this.managerPhone);
        tenant.setManagerEmail(this.managerEmail);
        return tenant;
    }

    public Tenant.TenantType getTenantType() {
        return tenantType;
    }

    public void setTenantType(Tenant.TenantType tenantType) {
        this.tenantType = tenantType;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }
}

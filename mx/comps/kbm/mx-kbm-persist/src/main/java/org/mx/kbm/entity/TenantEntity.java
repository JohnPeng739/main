package org.mx.kbm.entity;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.dal.entity.BaseDictTreeEntity;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * 描述： 知识租户实体类，基于Mongodb实现。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午7:43
 */
@Document(collection = "tenant")
public class TenantEntity extends BaseDictTreeEntity implements Tenant {
    @Indexed
    private TenantType tenantType = TenantType.PERSONAL;
    private String managerName = null, managerPhone = null, managerEmail = null;
    @DBRef
    private Set<Account> member = null;

    /**
     * {@inheritDoc}
     *
     * @see Tenant#getTenantMember()
     */
    @Override
    public Set<Account> getTenantMember() {
        return this.member;
    }

    /**
     * {@inheritDoc}
     *
     * @see Tenant#getTenantType()
     */
    @Override
    public TenantType getTenantType() {
        return tenantType;
    }

    /**
     * {@inheritDoc}
     *
     * @see Tenant#setTenantType(TenantType)
     */
    @Override
    public void setTenantType(TenantType tenantType) {
        this.tenantType = tenantType;
    }

    /**
     * {@inheritDoc}
     *
     * @see Tenant#getManagerName()
     */
    @Override
    public String getManagerName() {
        return managerName;
    }

    /**
     * {@inheritDoc}
     *
     * @see Tenant#setManagerName(String)
     */
    @Override
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    /**
     * {@inheritDoc}
     *
     * @see Tenant#getManagerPhone()
     */
    @Override
    public String getManagerPhone() {
        return managerPhone;
    }

    /**
     * {@inheritDoc}
     *
     * @see Tenant#setManagerPhone(String)
     */
    @Override
    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }

    /**
     * {@inheritDoc}
     *
     * @see Tenant#getManagerEmail()
     */
    @Override
    public String getManagerEmail() {
        return managerEmail;
    }

    /**
     * {@inheritDoc}
     *
     * @see Tenant#setManagerEmail(String)
     */
    @Override
    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }
}

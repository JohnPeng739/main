package org.mx.kbm.entity;

import org.mx.dal.entity.BaseDictEntity;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 描述： 知识租户实体类，基于Mongodb实现。
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午7:43
 */
@Document(collection = "tenant")
public class TenantEntity extends BaseDictEntity implements Tenant {
    private String managerName = null, managerPhone = null, managerEmail = null;

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

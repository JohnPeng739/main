package org.mx.kbm.service.bean;

import org.mx.dal.entity.BaseDict;
import org.mx.kbm.entity.KnowledgeContact;

/**
 * 描述： 联系人详细信息对象类定义
 *
 * @author John.Peng
 *         Date time 2018/4/30 上午11:47
 */
public class ContactDetailsBean {
    private KnowledgeContact contact;
    private BaseDict managedTenant, belongsTenant;

    public KnowledgeContact getContact() {
        return contact;
    }

    public BaseDict getManagedTenant() {
        return managedTenant;
    }

    public BaseDict getBelongsTenant() {
        return belongsTenant;
    }

    public void setContact(KnowledgeContact contact) {
        this.contact = contact;
    }

    public void setManagedTenant(BaseDict managedTenant) {
        this.managedTenant = managedTenant;
    }

    public void setBelongsTenant(BaseDict belongsTenant) {
        this.belongsTenant = belongsTenant;
    }
}

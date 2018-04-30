package org.mx.kbm.rest.vo;

import org.mx.kbm.entity.KnowledgeContact;
import org.mx.kbm.service.bean.ContactDetailsBean;
import org.mx.service.rest.vo.BaseDictVO;
import org.mx.service.rest.vo.BaseVO;

/**
 * 描述： 联系人详细信息值对象类定义，包括该联系人关联的租户。
 *
 * @author John.Peng
 *         Date time 2018/4/30 上午11:38
 */
public class ContactDetailsVO extends ContactVO {
    private BaseDictVO managedTenant, belongsTenant; // 管理的租户和所属的租户，管理的租户优先

    public static ContactDetailsVO transform(ContactDetailsBean contactDetailsBean) {
        if (contactDetailsBean == null || contactDetailsBean.getContact() == null) {
            return null;
        }
        KnowledgeContact contact = contactDetailsBean.getContact();
        ContactDetailsVO contactDetailsVO = new ContactDetailsVO();
        BaseVO.transform(contact, contactDetailsVO);
        contactDetailsVO.setCode(contact.getAccount().getCode());
        contactDetailsVO.setName(contact.getAccount().getName());
        contactDetailsVO.setAccountId(contact.getAccount().getId());
        contactDetailsVO.setAddress(contact.getAddress());
        contactDetailsVO.setEmail(contact.getEmail());
        contactDetailsVO.setMobile(contact.getMobile());
        if (contactDetailsBean.getManagedTenant() != null) {
            BaseDictVO managedTenant = new BaseDictVO();
            BaseDictVO.transform(contactDetailsBean.getManagedTenant(), managedTenant);
            contactDetailsVO.setManagedTenant(managedTenant);
        }
        if (contactDetailsBean.getBelongsTenant() != null) {
            BaseDictVO belongsTenant = new BaseDictVO();
            BaseDictVO.transform(contactDetailsBean.getBelongsTenant(), belongsTenant);
            contactDetailsVO.setBelongsTenant(belongsTenant);
        }
        return contactDetailsVO;
    }

    public BaseDictVO getManagedTenant() {
        return managedTenant;
    }

    public void setManagedTenant(BaseDictVO managedTenant) {
        this.managedTenant = managedTenant;
    }

    public BaseDictVO getBelongsTenant() {
        return belongsTenant;
    }

    public void setBelongsTenant(BaseDictVO belongsTenant) {
        this.belongsTenant = belongsTenant;
    }
}

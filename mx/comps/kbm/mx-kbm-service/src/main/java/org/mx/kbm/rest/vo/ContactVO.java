package org.mx.kbm.rest.vo;

import org.mx.kbm.entity.KnowledgeContact;
import org.mx.service.rest.vo.BaseVO;

/**
 * 描述： 知识账户值对象类定义
 *
 * @author John.Peng
 *         Date time 2018/4/29 下午8:39
 */
public class ContactVO extends BaseVO {
    private String accountId, code, name, mobile, email, address;

    public static ContactVO transform(KnowledgeContact contact) {
        if (contact == null) {
            return null;
        }
        ContactVO contactVO = new ContactVO();
        BaseVO.transform(contact, contactVO);
        contactVO.setMobile(contact.getMobile());
        contactVO.setEmail(contact.getEmail());
        contactVO.setAddress(contact.getAddress());
        if (contact.getAccount() != null) {
            contactVO.setAccountId(contact.getAccount().getId());
            contactVO.setCode(contact.getAccount().getCode());
            contactVO.setName(contact.getAccount().getName());
        }
        return contactVO;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

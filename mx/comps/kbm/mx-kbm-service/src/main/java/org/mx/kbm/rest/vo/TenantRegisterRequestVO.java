package org.mx.kbm.rest.vo;

import org.mx.kbm.service.bean.ContactRegisterRequest;
import org.mx.kbm.service.bean.TenantRegisterRequest;

/**
 * 描述： 知识租户注册请求值对象定义
 *
 * @author John.Peng
 *         Date time 2018/4/29 下午8:47
 */
public class TenantRegisterRequestVO {
    private String code, name, desc;
    private ContactRegisterRequest contact;

    public TenantRegisterRequest get() {
        TenantRegisterRequest registerRequest = new TenantRegisterRequest();
        registerRequest.setCode(code);
        registerRequest.setName(name);
        registerRequest.setDesc(desc);
        if (contact != null) {
            ContactRegisterRequest contactRequest = new ContactRegisterRequest();
            contactRequest.setCode(contact.getCode());
            contactRequest.setName(contact.getName());
            contactRequest.setPassword(contact.getPassword());
            contactRequest.setDesc(contact.getDesc());
            contactRequest.setAddress(contact.getAddress());
            contactRequest.setEmail(contact.getEmail());
            contactRequest.setMobile(contact.getMobile());
        }
        return registerRequest;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ContactRegisterRequest getContact() {
        return contact;
    }

    public void setContact(ContactRegisterRequest contact) {
        this.contact = contact;
    }
}

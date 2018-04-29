package org.mx.kbm.service.bean;

import org.mx.kbm.entity.KnowledgeTenant;

/**
 * 描述： 知识租户注册请求对象类定义
 *
 * @author John.Peng
 *         Date time 2018/4/29 下午9:07
 */
public class TenantRegisterRequest {
    private String code, name, desc; // 租户代码、租户名称、租户描述
    private KnowledgeTenant.TenantType type;
    private ContactRegisterRequest contact; // 租户联系人

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

    public KnowledgeTenant.TenantType getType() {
        return type;
    }

    public void setType(KnowledgeTenant.TenantType type) {
        this.type = type;
    }

    public ContactRegisterRequest getContact() {
        return contact;
    }

    public void setContact(ContactRegisterRequest contact) {
        this.contact = contact;
    }
}

package org.mx.kbm.service.bean;

import org.mx.kbm.entity.KnowledgeTenant;

import java.util.List;

/**
 * 描述： 知识租户信息对象类定义
 *
 * @author John.Peng
 *         Date time 2018/5/1 上午9:46
 */
public class TenantBean {
    private String id, code, name, desc;
    private KnowledgeTenant.TenantType type;
    private List<ContactDetailsBean> members;
    private ContactDetailsBean contact;

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public KnowledgeTenant.TenantType getType() {
        return type;
    }

    public List<ContactDetailsBean> getMembers() {
        return members;
    }

    public ContactDetailsBean getContact() {
        return contact;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setType(KnowledgeTenant.TenantType type) {
        this.type = type;
    }

    public void setMembers(List<ContactDetailsBean> members) {
        this.members = members;
    }

    public void setContact(ContactDetailsBean contact) {
        this.contact = contact;
    }
}

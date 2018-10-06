package org.mx.tools.ffee.service.bean;

import java.util.List;

public class FamilyInfoBean {
    private String id, name, avatarUrl, desc;
    private List<FamilyMemberInfoBean> members;
    private boolean valid;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getDesc() {
        return desc;
    }

    public List<FamilyMemberInfoBean> getMembers() {
        return members;
    }

    public boolean isValid() {
        return valid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setMembers(List<FamilyMemberInfoBean> members) {
        this.members = members;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}

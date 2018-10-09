package org.mx.tools.ffee.service.bean;

public class FamilyInfoBean {
    private String id, name, avatarUrl, desc, ownerId, role;
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

    public String getOwnerId() {
        return ownerId;
    }

    public String getRole() {
        return role;
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

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

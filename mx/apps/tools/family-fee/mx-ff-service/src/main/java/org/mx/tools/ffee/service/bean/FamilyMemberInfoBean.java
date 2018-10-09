package org.mx.tools.ffee.service.bean;

public class FamilyMemberInfoBean {
    private String familyId, id, role, accountId;
    private boolean isOwner;

    public String getFamilyId() {
        return familyId;
    }

    public String getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getAccountId() {
        return accountId;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }
}

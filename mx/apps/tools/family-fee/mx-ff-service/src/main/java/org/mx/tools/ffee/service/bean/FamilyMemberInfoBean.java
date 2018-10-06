package org.mx.tools.ffee.service.bean;

/**
 * 描述：
 *
 * @author : john date : 2018/10/6 下午4:50
 */
public class FamilyMemberInfoBean {
    private String id, role, accountId;
    private boolean isOwner;

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

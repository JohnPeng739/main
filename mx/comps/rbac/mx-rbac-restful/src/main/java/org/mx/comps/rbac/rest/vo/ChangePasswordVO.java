package org.mx.comps.rbac.rest.vo;

/**
 * Created by john on 2017/11/12.
 */
public class ChangePasswordVO {
    private String oldPassword, newPassword;

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}

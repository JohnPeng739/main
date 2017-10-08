package com.ds.retl.rest.vo.user;

/**
 * Created by john on 2017/10/8.
 */
public class ChangePasswordVO {
    String oldPassword, newPassword;

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

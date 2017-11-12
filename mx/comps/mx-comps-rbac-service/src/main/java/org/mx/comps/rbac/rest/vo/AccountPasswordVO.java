package org.mx.comps.rbac.rest.vo;

/**
 * Created by john on 2017/11/12.
 */
public class AccountPasswordVO {
    private String accountCode, password;

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public String getPassword() {
        return password;
    }
}

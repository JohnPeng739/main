package org.mx.comps.rbac.rest.vo;

/**
 * 采用账户/密码认证的值对象定义
 *
 * @author : john.peng created on date : 2017/12/03
 */
public class AuthenticateAccountPasswordVO {
    private String accountCode, password;
    private boolean forcedReplace = false;

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isForcedReplace() {
        return forcedReplace;
    }

    public void setForcedReplace(boolean forcedReplace) {
        this.forcedReplace = forcedReplace;
    }
}

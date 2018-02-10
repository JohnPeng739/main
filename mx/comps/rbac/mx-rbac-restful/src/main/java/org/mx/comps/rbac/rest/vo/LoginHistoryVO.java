package org.mx.comps.rbac.rest.vo;

import org.mx.comps.rbac.dal.entity.LoginHistory;
import org.mx.service.rest.vo.BaseVO;

import java.util.*;

public class LoginHistoryVO extends BaseVO {
    private AccountVO account;
    private String token;
    private long loginTime, logoutTime;
    private boolean online;

    public static LoginHistoryVO transform(LoginHistory loginHistory) {
        if (loginHistory == null) {
            return null;
        }
        LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
        BaseVO.transform(loginHistory, loginHistoryVO);
        loginHistoryVO.loginTime = loginHistory.getLoginTime();
        loginHistoryVO.logoutTime = loginHistory.getLogoutTime();
        loginHistoryVO.online = loginHistory.isOnline();
        loginHistoryVO.token = loginHistory.getToken();
        if (loginHistory.getAccount() != null) {
            loginHistoryVO.account = AccountVO.transform(loginHistory.getAccount(), true);
        }
        return loginHistoryVO;
    }

    public static List<LoginHistoryVO> transform(Collection<LoginHistory> loginHistories) {
        List<LoginHistoryVO> loginHistoryVOS = new ArrayList<>();
        if (loginHistories == null || loginHistories.isEmpty()) {
            return loginHistoryVOS;
        }
        loginHistories.forEach(loginHistory -> {
            LoginHistoryVO loginHistoryVO = LoginHistoryVO.transform(loginHistory);
            if (loginHistoryVO != null) {
                loginHistoryVOS.add(loginHistoryVO);
            }
        });
        return loginHistoryVOS;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AccountVO getAccount() {
        return account;
    }

    public void setAccount(AccountVO account) {
        this.account = account;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public long getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(long logoutTime) {
        this.logoutTime = logoutTime;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}

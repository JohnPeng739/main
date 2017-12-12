package org.mx.comps.rbac.rest.vo;

import org.mx.comps.rbac.dal.entity.LoginHistory;
import org.mx.service.rest.vo.BaseVO;

import java.util.ArrayList;
import java.util.List;

public class LoginHistoryVO extends BaseVO {
    private AccountVO account;
    private long loginTime, logoutTime;
    private boolean online;

    public static void transform(LoginHistory loginHistory, LoginHistoryVO loginHistoryVO) {
        if (loginHistory == null || loginHistoryVO == null) {
            return;
        }
        BaseVO.transform(loginHistory, loginHistoryVO);
        loginHistoryVO.loginTime = loginHistory.getLoginTime();
        loginHistoryVO.logoutTime = loginHistory.getLogoutTime();
        loginHistoryVO.online = loginHistory.isOnline();
        if (loginHistory.getAccount() != null) {
            AccountVO accountVO = new AccountVO();
            AccountVO.transform(loginHistory.getAccount(), accountVO);
            loginHistoryVO.account = accountVO;
        }
    }

    public static List<LoginHistoryVO> transformLoginHistories(List<LoginHistory> loginHistories) {
        if (loginHistories == null || loginHistories.isEmpty()) {
            return null;
        }
        List<LoginHistoryVO> loginHistoryVOs = new ArrayList<>(loginHistories.size());
        for (LoginHistory loginHistory : loginHistories) {
            LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
            LoginHistoryVO.transform(loginHistory, loginHistoryVO);
            loginHistoryVOs.add(loginHistoryVO);
        }
        return loginHistoryVOs;
    }

    public void setAccount(AccountVO account) {
        this.account = account;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public void setLogoutTime(long logoutTime) {
        this.logoutTime = logoutTime;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public AccountVO getAccount() {
        return account;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public long getLogoutTime() {
        return logoutTime;
    }

    public boolean isOnline() {
        return online;
    }
}

package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.AccessLog;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.service.bean.AccountInfoBean;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface AccountService {
    AccountSummary registry(AccountInfoBean accountInfoBean);

    AccountSummary saveAccount(AccountInfoBean accountInfoBean);

    AccountSummary getAccountSummaryById(String accountId);

    AccountSummary getAccountSummaryByOpenId(String openId);

    void writeAccessLog(String content);

    List<AccessLog> getLogsByAccount(String accountId);

    String changeAccountAvatar(String accountId, InputStream in);

    File getAccountAvatar(String accountId);

    class AccountSummary {
        private FfeeAccount account;
        private Family family;

        public AccountSummary(FfeeAccount account, Family family) {
            super();
            this.account = account;
            this.family = family;
        }

        public FfeeAccount getAccount() {
            return account;
        }

        public Family getFamily() {
            return family;
        }
    }
}

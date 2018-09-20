package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.AccessLog;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.dal.entity.FfeeAccount;

import java.util.List;

public interface AccountService {
    FfeeAccount registry(FfeeAccount account);

    FfeeAccount modifyAccount(FfeeAccount account);

    FfeeAccount getAccountById(String accountId);

    AccountSummary getAccountSummaryByOpenId(String openId);

    List<AccessLog> getLogsByAccount(String accountId);

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

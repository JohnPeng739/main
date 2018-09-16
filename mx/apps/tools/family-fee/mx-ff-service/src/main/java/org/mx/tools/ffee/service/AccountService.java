package org.mx.tools.ffee.service;

import org.mx.tools.ffee.dal.entity.AccessLog;
import org.mx.tools.ffee.dal.entity.FfeeAccount;

import java.util.List;

public interface AccountService {
    FfeeAccount registry(FfeeAccount account);

    FfeeAccount modifyAccount(FfeeAccount account);

    FfeeAccount getAccountById(String accountId);

    List<AccessLog> getLogsByAccount(String accountId);
}

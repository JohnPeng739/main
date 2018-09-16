package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.tools.ffee.dal.entity.AccessLog;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("accountService")
public class AccountServiceImpl implements AccountService {
    private static final Log logger = LogFactory.getLog(AccountServiceImpl.class);

    private GeneralAccessor generalAccessor;

    @Autowired
    public AccountServiceImpl(@Qualifier("generalAccessor") GeneralAccessor generalAccessor) {
        super();
        this.generalAccessor = generalAccessor;
    }

    @Override
    public FfeeAccount registry(FfeeAccount account) {
        FfeeAccount checked = generalAccessor.getById(account.getId(), FfeeAccount.class);
        if (checked != null) {
            checked.setNickname(account.getNickname());
            checked.setMobile(account.getMobile());
            checked.setEmail(account.getEmail());
            checked.setWx(account.getWx());
            checked.setQq(account.getQq());
            checked.setWb(account.getWb());
            checked.setAvatarUrl(account.getAvatarUrl());
            checked.setCountry(account.getCountry());
            checked.setProvince(account.getProvince());
            checked.setCity(account.getCity());
            account = checked;
        }
        return generalAccessor.save(account);
    }

    @Override
    public FfeeAccount modifyAccount(FfeeAccount account) {
        FfeeAccount checked = generalAccessor.getById(account.getId(), FfeeAccount.class);
        if (checked == null) {
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED
            );
        }
        checked.setNickname(account.getNickname());
        checked.setMobile(account.getMobile());
        checked.setEmail(account.getEmail());
        checked.setWx(account.getWx());
        checked.setQq(account.getQq());
        checked.setWb(account.getWb());
        checked.setAvatarUrl(account.getAvatarUrl());
        checked.setCountry(account.getCountry());
        checked.setProvince(account.getProvince());
        checked.setCity(account.getCity());
        return generalAccessor.save(checked);
    }

    @Override
    public FfeeAccount getAccountById(String accountId) {
        return generalAccessor.getById(accountId, FfeeAccount.class);
    }

    @Override
    public List<AccessLog> getLogsByAccount(String accountId) {
        FfeeAccount account = generalAccessor.getById(accountId, FfeeAccount.class);
        if (account == null) {
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED
            );
        }
        return generalAccessor.find(GeneralAccessor.ConditionTuple.eq("account", account), AccessLog.class);
    }
}

package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.service.GeneralAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.AccessLog;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.repository.FamilyRepository;
import org.mx.tools.ffee.service.AccountService;
import org.mx.tools.ffee.service.FileTransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component("accountService")
public class AccountServiceImpl implements AccountService {
    private static final Log logger = LogFactory.getLog(AccountServiceImpl.class);

    private GeneralAccessor generalAccessor;
    private FamilyRepository familyRepository;
    private FileTransportService fileTransportService;

    @Autowired
    public AccountServiceImpl(@Qualifier("generalAccessor") GeneralAccessor generalAccessor,
                              FamilyRepository familyRepository,
                              FileTransportService fileTransportService) {
        super();
        this.generalAccessor = generalAccessor;
        this.familyRepository = familyRepository;
        this.fileTransportService = fileTransportService;
    }

    @Transactional
    @Override
    public FfeeAccount registry(FfeeAccount account) {
        // 通过微信的openId和注册Mobile号码判定是否已经有注册过
        if (!StringUtils.isBlank(account.getOpenId()) || !StringUtils.isBlank(account.getMobile())) {
            FfeeAccount checked = generalAccessor.findOne(GeneralAccessor.ConditionGroup.or(
                    GeneralAccessor.ConditionTuple.eq("openId", account.getOpenId()),
                    GeneralAccessor.ConditionTuple.eq("mobile", account.getMobile())
            ), FfeeAccount.class);
            if (checked != null) {
                if (logger.isWarnEnabled()) {
                    logger.warn(String.format("The account has registry, open id: %s, mobile: %s.",
                            account.getOpenId(), account.getMobile()));
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
                account = checked;
            }
        }
        return generalAccessor.save(account);
    }

    @Transactional
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

    @Transactional(readOnly = true)
    @Override
    public FfeeAccount getAccountById(String accountId) {
        return generalAccessor.getById(accountId, FfeeAccount.class);
    }

    @Transactional(readOnly = true)
    @Override
    public AccountSummary getAccountSummaryByOpenId(String openId) {
        if (StringUtils.isBlank(openId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The account's open id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        FfeeAccount account = generalAccessor.findOne(
                GeneralAccessor.ConditionTuple.eq("openId", openId), FfeeAccount.class);
        Family family = null;
        if (account != null) {
            String familyId = familyRepository.findFamilyIdByOpenId(openId);
            if (!StringUtils.isBlank(familyId)) {
                family = generalAccessor.getById(familyId, Family.class);
            }
        }
        return new AccountSummary(account, family);
    }

    @Transactional(readOnly = true)
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

    @Transactional
    @Override
    public String changeAccountAvatar(String accountId, InputStream in) {
        FfeeAccount account = generalAccessor.getById(accountId, FfeeAccount.class);
        if (account == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The account[%s] not found.", accountId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        String root = System.getProperty("user.dir");
        Path path = Paths.get(String.format("%s/account/avatar-%s.png", root, accountId));
        String filePath = fileTransportService.uploadFile(path.toString(), in);
        account.setAvatarUrl(filePath.substring(root.length()));
        account = generalAccessor.save(account);
        return account.getAvatarUrl();
    }

    @Transactional(readOnly = true)
    @Override
    public File getAccountAvatar(String accountId) {
        FfeeAccount account = generalAccessor.getById(accountId, FfeeAccount.class);
        if (account == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The account[%s] not found.", accountId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        Path path = Paths.get(String.format("%s/account/avatar-%s.png", System.getProperty("user.dir"), accountId));
        return fileTransportService.downloadFile(path.toString());
    }
}

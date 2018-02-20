package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.service.FfeeAccountManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

/**
 * 描述： FFEE账户管理服务实现类，基于Hibernate实现。
 *
 * @author John.Peng
 *         Date time 2018/2/19 上午11:38
 */
@Component
public class FfeeAccountManageServiceImpl implements FfeeAccountManageService {
    private static final Log logger = LogFactory.getLog(FfeeAccountManageServiceImpl.class);

    @Autowired
    @Qualifier("generalDictAccessor")
    private GeneralDictAccessor accessor = null;

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * 使用用户名、密码方式注册账户。
     *
     * @see FfeeAccountManageService#regist(String, String, String)
     */
    @Override
    @Transactional()
    public FfeeAccount regist(String code, String name, String password) {
        if (StringUtils.isBlank(code) || StringUtils.isBlank(name) || StringUtils.isBlank(password)) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        Account account = accessor.getByCode(code, Account.class);
        if (account != null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_HAS_EXIST);
        }
        Role role = accessor.getByCode("user", Role.class);
        if (role == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ROLE_NOT_FOUND);
        }
        account = EntityFactory.createEntity(Account.class);
        account.setCode(code);
        account.setName(name);
        try {
            account.setPassword(DigestUtils.md5(password));
        } catch (NoSuchAlgorithmException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Digest the password fail.", ex);
            }
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_DIGEST_PASSWORD_FAIL);
        }
        account = accessor.save(account, false);
        FfeeAccount ffeeAccount = EntityFactory.createEntity(FfeeAccount.class);
        ffeeAccount.setAccount(account);
        ffeeAccount.setSourceType(FfeeAccount.AccountSourceType.NORMAL);
        ffeeAccount = accessor.save(ffeeAccount, false);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("常规账户[%s]注册成功。", name));
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Create a normal registry FFEE account[%s - %s] successfully.", code, name));
        }
        return ffeeAccount;
    }
}

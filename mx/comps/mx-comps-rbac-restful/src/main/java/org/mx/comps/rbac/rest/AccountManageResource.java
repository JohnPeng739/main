package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.LoginHistory;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.rest.vo.AccountPasswordVO;
import org.mx.comps.rbac.rest.vo.AccountVO;
import org.mx.comps.rbac.rest.vo.ChangePasswordVO;
import org.mx.comps.rbac.rest.vo.LoginHistoryVO;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceException;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.rest.vo.DataVO;
import org.mx.rest.vo.PaginationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountManageResource {
    private static final Log logger = LogFactory.getLog(AccountManageResource.class);

    @Autowired
    private AccountManageService accountManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("loginHistories")
    @GET
    public DataVO<List<LoginHistoryVO>> loginHistories() {
        try {
            List<LoginHistory> histories = accountManageService.list(LoginHistory.class);
            List<LoginHistoryVO> vos = LoginHistoryVO.transformLoginHistories(histories);
            return new DataVO(vos);
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(ex);
        }
    }

    @Path("loginHistories")
    @POST
    public PaginationDataVO<List<LoginHistoryVO>> loginHistories(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<LoginHistory> histories = accountManageService.list(pagination, LoginHistory.class);
            List<LoginHistoryVO> vos = LoginHistoryVO.transformLoginHistories(histories);
            return new PaginationDataVO(pagination, vos);
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(ex);
        }
    }

    @Path("accounts")
    @GET
    public DataVO<List<AccountVO>> listAccounts() {
        try {
            List<Account> accounts = accountManageService.list(Account.class);
            return new DataVO<>(AccountVO.transformAccountVOs(accounts));
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(ex);
        }
    }

    @Path("accounts")
    @POST
    public PaginationDataVO<List<AccountVO>> pagingAccounts(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<Account> accounts = accountManageService.list(pagination, Account.class);
            return new PaginationDataVO<>(pagination, AccountVO.transformAccountVOs(accounts));
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(ex);
        }
    }

    @Path("accounts/new")
    @POST
    public DataVO<AccountVO> newAccount(@QueryParam("userCode") String userCode, AccountVO accountVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Account account = EntityFactory.createEntity(Account.class);
            AccountVO.transform(accountVO, account);
            account = accountManageService.save(account);
            accountVO = new AccountVO();
            AccountVO.transform(account, accountVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(accountVO);
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(ex);
        }
    }

    @Path("accounts/{id}")
    @PUT
    public DataVO<AccountVO> saveAccount(@PathParam("id") String id, @QueryParam("userCode") String userCode, AccountVO accountVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Account account = EntityFactory.createEntity(Account.class);
            AccountVO.transform(accountVO, account);
            account.setId(id);
            account = accountManageService.save(account);
            accountVO = new AccountVO();
            AccountVO.transform(account, accountVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(accountVO);
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(ex);
        }
    }

    @Path("accounts/{id}")
    @DELETE
    public DataVO<AccountVO> invalidateAccount(@PathParam("id") String id, @QueryParam("userCode") String userCode) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Account account = accountManageService.remove(id, Account.class);
            AccountVO accountVO = new AccountVO();
            AccountVO.transform(account, accountVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(accountVO);
        } catch (UserInterfaceException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO(ex);
        }
    }

    @Path("accounts/{id}/password/change")
    @POST
    public DataVO<AccountVO> changePassword(@PathParam("id") String id, @QueryParam("userCode") String userCode, ChangePasswordVO vo) {
        sessionDataStore.setCurrentUserCode(userCode);
        if (vo == null) {
            return new DataVO<>(new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM));
        }
        String oldPassword = vo.getOldPassword();
        String newPassword = vo.getNewPassword();
        try {
            Account account = accountManageService.changePassword(id, oldPassword, newPassword);
            AccountVO accountVO = new AccountVO();
            AccountVO.transform(account, accountVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(accountVO);
        } catch (UserInterfaceRbacErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("accounts/login")
    @POST
    public DataVO<LoginHistoryVO> login(@QueryParam("userCode") String userCode, AccountPasswordVO vo) {
        sessionDataStore.setCurrentUserCode(userCode);
        if (vo == null) {
            return new DataVO<>(new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM));
        }
        String accountCode = vo.getAccountCode(), password = vo.getPassword();
        boolean forced = vo.isForced();
        try {
            LoginHistory loginHistory = accountManageService.login(accountCode, password, forced);
            LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
            LoginHistoryVO.transform(loginHistory, loginHistoryVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(loginHistoryVO);
        } catch (UserInterfaceRbacErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("accounts/{id}/logout")
    @GET
    public DataVO<LoginHistoryVO> logout(@PathParam("id") String id, @QueryParam("userCode") String userCode) {
        sessionDataStore.setCurrentUserCode(userCode);
        if (StringUtils.isEmpty(id)) {
            return new DataVO<>(new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM));
        }
        try {
            LoginHistory loginHistory = accountManageService.logout(id);
            LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
            LoginHistoryVO.transform(loginHistory, loginHistoryVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(loginHistoryVO);
        } catch (UserInterfaceRbacErrorException ex) {
            return new DataVO<>(ex);
        }
    }
}

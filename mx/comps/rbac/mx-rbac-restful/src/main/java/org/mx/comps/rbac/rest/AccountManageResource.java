package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.LoginHistory;
import org.mx.comps.rbac.rest.vo.*;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.dal.Pagination;
import org.mx.dal.entity.OperateLog;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceException;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.service.rest.vo.PaginationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
    @Qualifier("generalAccessor")
    private GeneralAccessor accessor = null;

    @Autowired
    private AccountManageService accountManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("logs")
    @GET
    public DataVO<List<OperateLogVO>> logs() {
        try {
            List<OperateLog> logs = accessor.list(OperateLog.class);
            List<OperateLogVO> vos = OperateLogVO.transformLogVOs(logs);
            return new DataVO(vos);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("logs")
    @POST
    public PaginationDataVO<List<OperateLogVO>> logs(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<OperateLog> logs = accessor.list(pagination, OperateLog.class);
            List<OperateLogVO> vos = OperateLogVO.transformLogVOs(logs);
            return new PaginationDataVO(pagination, vos);
        } catch (UserInterfaceException ex) {
            return new PaginationDataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("loginHistories")
    @GET
    public DataVO<List<LoginHistoryVO>> loginHistories() {
        try {
            List<LoginHistory> histories = accessor.list(LoginHistory.class);
            List<LoginHistoryVO> vos = LoginHistoryVO.transformLoginHistories(histories);
            return new DataVO(vos);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("loginHistories")
    @POST
    public PaginationDataVO<List<LoginHistoryVO>> loginHistories(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<LoginHistory> histories = accessor.list(pagination, LoginHistory.class);
            List<LoginHistoryVO> vos = LoginHistoryVO.transformLoginHistories(histories);
            return new PaginationDataVO(pagination, vos);
        } catch (UserInterfaceException ex) {
            return new PaginationDataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accounts")
    @GET
    public DataVO<List<AccountVO>> listAccounts() {
        try {
            List<Account> accounts = accessor.list(Account.class);
            return new DataVO<>(AccountVO.transformAccountVOs(accounts));
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accounts")
    @POST
    public PaginationDataVO<List<AccountVO>> pagingAccounts(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<Account> accounts = accessor.list(pagination, Account.class);
            return new PaginationDataVO<>(pagination, AccountVO.transformAccountVOs(accounts));
        } catch (UserInterfaceException ex) {
            return new PaginationDataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accounts/{id}")
    @PUT
    public DataVO<AccountVO> saveAccount(@PathParam("id") String id, @QueryParam("userCode") String userCode,
                                         AccountInfoVO accountInfoVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            accountInfoVO.setAccountId(id);
            Account account = accountManageService.saveAccount(accountInfoVO.getAccountInfo());
            AccountVO accountVO = new AccountVO();
            AccountVO.transform(account, accountVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(accountVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accounts/{id}")
    @DELETE
    public DataVO<AccountVO> invalidateAccount(@PathParam("id") String id, @QueryParam("userCode") String userCode) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Account account = accessor.remove(id, Account.class);
            AccountVO accountVO = new AccountVO();
            AccountVO.transform(account, accountVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(accountVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
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
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accounts/login")
    @POST
    public DataVO<LoginHistoryVO> login(@QueryParam("userCode") String userCode, AuthenticateAccountPasswordVO vo) {
        sessionDataStore.setCurrentUserCode(userCode);
        String accountCode = vo.getAccountCode(), password = vo.getPassword();
        boolean forced = vo.isForcedReplace();
        try {
            LoginHistory loginHistory = accountManageService.login(accountCode, password, forced);
            LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
            LoginHistoryVO.transform(loginHistory, loginHistoryVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(loginHistoryVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accounts/{id}/logout")
    @GET
    public DataVO<LoginHistoryVO> logout(@PathParam("id") String id, @QueryParam("userCode") String userCode) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            LoginHistory loginHistory = accountManageService.logout(id);
            LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
            LoginHistoryVO.transform(loginHistory, loginHistoryVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(loginHistoryVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }
}

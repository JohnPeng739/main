package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.jwt.AuthenticateAround;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountManageResource {
    private static final Log logger = LogFactory.getLog(AccountManageResource.class);

    private GeneralAccessor accessor;

    private AccountManageService accountManageService;

    private SessionDataStore sessionDataStore;

    @Autowired
    public AccountManageResource(@Qualifier("generalAccessor") GeneralAccessor accessor,
                                 AccountManageService accountManageService, SessionDataStore sessionDataStore) {
        super();
        this.accessor = accessor;
        this.accountManageService = accountManageService;
        this.sessionDataStore = sessionDataStore;
    }

    @Path("logs")
    @GET
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<List<OperateLogVO>> logs(@Context Request request) {
        try {
            List<OperateLog> logs = accessor.list(OperateLog.class);
            List<OperateLogVO> vos = OperateLogVO.transform(logs);
            return new DataVO<>(vos);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("List logs fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("logs")
    @POST
    @AuthenticateAround(returnValueClass = PaginationDataVO.class)
    public PaginationDataVO<List<OperateLogVO>> logs(Pagination pagination, @Context Request request) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<OperateLog> logs = accessor.list(pagination, OperateLog.class);
            List<OperateLogVO> vos = OperateLogVO.transform(logs);
            return new PaginationDataVO<>(pagination, vos);
        } catch (UserInterfaceException ex) {
            return new PaginationDataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("List logs fail.", ex);
            }
            return new PaginationDataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("loginHistories")
    @GET
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<List<LoginHistoryVO>> loginHistories() {
        try {
            List<LoginHistory> histories = accessor.list(LoginHistory.class);
            List<LoginHistoryVO> vos = LoginHistoryVO.transform(histories);
            return new DataVO<>(vos);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("List login histories fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("loginHistories")
    @POST
    @AuthenticateAround(returnValueClass = PaginationDataVO.class)
    public PaginationDataVO<List<LoginHistoryVO>> loginHistories(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<LoginHistory> histories = accessor.list(pagination, LoginHistory.class);
            List<LoginHistoryVO> vos = LoginHistoryVO.transform(histories);
            return new PaginationDataVO<>(pagination, vos);
        } catch (UserInterfaceException ex) {
            return new PaginationDataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("List login histories fail.", ex);
            }
            return new PaginationDataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accounts")
    @GET
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<List<AccountVO>> listAccounts() {
        try {
            List<Account> accounts = accessor.list(Account.class);
            return new DataVO<>(AccountVO.transform(accounts));
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("List accounts fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accounts")
    @POST
    @AuthenticateAround(returnValueClass = PaginationDataVO.class)
    public PaginationDataVO<List<AccountVO>> pagingAccounts(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<Account> accounts = accessor.list(pagination, Account.class);
            return new PaginationDataVO<>(pagination, AccountVO.transform(accounts));
        } catch (UserInterfaceException ex) {
            return new PaginationDataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("List accounts fail.", ex);
            }
            return new PaginationDataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accounts/{id}")
    @PUT
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<AccountVO> saveAccount(@PathParam("id") String id, @QueryParam("userCode") String userCode,
                                         AccountInfoVO accountInfoVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            accountInfoVO.setId(id);
            Account account = accountManageService.saveAccount(accountInfoVO.getAccountInfo());
            AccountVO accountVO = AccountVO.transform(account, true);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(accountVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Save account fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accounts/{id}")
    @DELETE
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<AccountVO> invalidateAccount(@PathParam("id") String id, @QueryParam("userCode") String userCode) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Account account = accessor.remove(id, Account.class);
            AccountVO accountVO = AccountVO.transform(account, true);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(accountVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Invalidate account fail.", ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accounts/{id}/password/change")
    @POST
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<AccountVO> changePassword(@PathParam("id") String id, @QueryParam("userCode") String userCode,
                                            ChangePasswordVO vo) {
        sessionDataStore.setCurrentUserCode(userCode);
        if (vo == null) {
            return new DataVO<>(new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM));
        }
        String oldPassword = vo.getOldPassword();
        String newPassword = vo.getNewPassword();
        try {
            Account account = accountManageService.changePassword(id, oldPassword, newPassword);
            AccountVO accountVO = AccountVO.transform(account, true);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(accountVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Change user[%s] password fail.", userCode), ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("accounts/{id}/personal/change")
    @POST
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<AccountVO> changePersonal(@PathParam("id") String id, @QueryParam("userCode") String userCode,
                                            ChangePersonalVO vo) {
        sessionDataStore.setCurrentUserCode(userCode);
        if (vo == null) {
            return new DataVO<>(new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM));
        }
        try {
            Account account = accountManageService.changePersonal(vo.getAccountPersonalInfo());
            AccountVO accountVO = AccountVO.transform(account, true);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(accountVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("login")
    @POST
    public DataVO<LoginHistoryVO> login(@Context Request request, @Context Response response,
                                        AuthenticateAccountPasswordVO vo) {
        sessionDataStore.setCurrentUserCode(vo.getAccountCode());
        String accountCode = vo.getAccountCode(), password = vo.getPassword();
        boolean forced = vo.isForcedReplace();
        try {
            LoginHistory loginHistory = accountManageService.login(accountCode, password, forced);
            LoginHistoryVO loginHistoryVO = LoginHistoryVO.transform(loginHistory);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(loginHistoryVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("User[%s] login fail.", vo.getAccountCode()), ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }

    @Path("logout/{id}")
    @GET
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<LoginHistoryVO> logout(@PathParam("id") String id, @QueryParam("userCode") String userCode,
                                         @Context Request request) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            LoginHistory loginHistory = accountManageService.logout(id);
            LoginHistoryVO loginHistoryVO = LoginHistoryVO.transform(loginHistory);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(loginHistoryVO);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("User[%s] logout fail.", userCode), ex);
            }
            return new DataVO<>(
                    new UserInterfaceSystemErrorException(
                            UserInterfaceSystemErrorException.SystemErrors.SYSTEM_OTHER_FAIL));
        }
    }
}

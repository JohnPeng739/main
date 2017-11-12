package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.LoginHistory;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
import org.mx.comps.rbac.error.UserInterfaceErrors;
import org.mx.comps.rbac.rest.vo.AccountPasswordVO;
import org.mx.comps.rbac.rest.vo.AccountVO;
import org.mx.comps.rbac.rest.vo.ChangePasswordVO;
import org.mx.comps.rbac.rest.vo.LoginHistoryVO;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.dal.Pagination;
import org.mx.dal.exception.EntityAccessException;
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

    @Path("accounts")
    @GET
    public DataVO<List<AccountVO>> listAccounts() {
        try {
            List<Account> accounts = accountManageService.list(Account.class);
            return new DataVO<>(AccountVO.transformAccountVOs(accounts));
        } catch (EntityAccessException ex) {
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
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
        } catch (EntityAccessException ex) {
            return new PaginationDataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("accounts/{id}/password/change")
    @POST
    public DataVO<AccountVO> changePassword(@PathParam("id") String id, ChangePasswordVO vo) {
        if (vo == null) {
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM));
        }
        String oldPassword = vo.getOldPassword();
        String newPassword = vo.getNewPassword();
        try {
            Account account = accountManageService.changePassword(id, oldPassword, newPassword);
            AccountVO accountVO = new AccountVO();
            AccountVO.transform(account, accountVO);
            return new DataVO<>(accountVO);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("accounts/login")
    @POST
    public DataVO<LoginHistoryVO> login(AccountPasswordVO vo) {
        if (vo == null) {
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM));
        }
        String accountCode = vo.getAccountCode();
        String password = vo.getPassword();
        try {
            LoginHistory loginHistory = accountManageService.login(accountCode, password);
            LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
            LoginHistoryVO.transform(loginHistory, loginHistoryVO);
            return new DataVO<>(loginHistoryVO);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("accounts/{id}/logout")
    @GET
    public DataVO<LoginHistoryVO> logout(@PathParam("id") String id) {
        if (StringUtils.isEmpty(id)) {
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM));
        }
        try {
            LoginHistory loginHistory = accountManageService.logout(id);
            LoginHistoryVO loginHistoryVO = new LoginHistoryVO();
            LoginHistoryVO.transform(loginHistory, loginHistoryVO);
            return new DataVO<>(loginHistoryVO);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }
}

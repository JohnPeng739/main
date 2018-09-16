package org.mx.tools.ffee.rest;

import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.ffee.dal.entity.AccessLog;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.rest.vo.AccountModifyVO;
import org.mx.tools.ffee.rest.vo.AccountRegistryVO;
import org.mx.tools.ffee.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component("accountManageResource")
@Path("rest/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountManageResource {
    private AccountService accountService;

    @Autowired
    public AccountManageResource(AccountService accountService) {
        super();
        this.accountService = accountService;
    }

    @Path("accounts/registry")
    @POST
    public DataVO<FfeeAccount> registry(AccountRegistryVO accountRegistryVO) {
        if (accountRegistryVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        FfeeAccount account = accountRegistryVO.get();
        return new DataVO<>(accountService.registry(account));
    }

    @Path("accounts/{accountId}")
    @PUT
    public DataVO<FfeeAccount> modifyAccount(AccountModifyVO accountModifyVO) {
        if (accountModifyVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        FfeeAccount account = accountModifyVO.get();
        return new DataVO<>(accountService.modifyAccount(account));
    }

    @Path("accounts/{accountId}")
    @GET
    public DataVO<FfeeAccount> getAccountById(@PathParam("accountId") String accountId) {
        if (StringUtils.isBlank(accountId)) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        return new DataVO<>(accountService.getAccountById(accountId));
    }

    @Path("accounts/{accountId}/logs")
    @GET
    public DataVO<List<AccessLog>> getLogsByAccount(@PathParam("accountId") String accountId) {
        if (StringUtils.isBlank(accountId)) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        return new DataVO<>(accountService.getLogsByAccount(accountId));
    }
}

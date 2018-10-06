package org.mx.tools.ffee.rest;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.ffee.dal.entity.AccessLog;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.rest.vo.AccountModifyVO;
import org.mx.tools.ffee.rest.vo.AccountRegistryVO;
import org.mx.tools.ffee.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.util.List;

@Path("rest/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountManageResource {
    private AccountService accountService;
    private SessionDataStore sessionDataStore;

    @Autowired
    public AccountManageResource(AccountService accountService, SessionDataStore sessionDataStore) {
        super();
        this.accountService = accountService;
        this.sessionDataStore = sessionDataStore;
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
    public DataVO<FfeeAccount> modifyAccount(@PathParam("accountId") String accountId,
                                             AccountModifyVO accountModifyVO) {
        if (accountModifyVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(accountModifyVO.getOpenId());
        FfeeAccount account = accountModifyVO.get();
        account.setId(accountId);
        return new DataVO<>(accountService.modifyAccount(account));
    }

    @Path("accounts/{accountId}")
    @GET
    public DataVO<AccountService.AccountSummary> getAccountById(@PathParam("accountId") String accountId) {
        return new DataVO<>(accountService.getAccountSummaryById(accountId));
    }

    @Path("account/summary")
    @GET
    public DataVO<AccountService.AccountSummary> getAccountSummary(@QueryParam("userCode") String userCode) {
        return new DataVO<>(accountService.getAccountSummaryByOpenId(userCode));
    }

    @Path("accounts/{accountId}/logs")
    @GET
    public DataVO<List<AccessLog>> getLogsByAccount(@PathParam("accountId") String accountId) {
        return new DataVO<>(accountService.getLogsByAccount(accountId));
    }

    @Path("accounts/{accountId}/avatar")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public DataVO<String> changeFamilyAvatar(@FormDataParam("file") InputStream in,
                                             @FormDataParam("file") FormDataContentDisposition detail,
                                             @PathParam("accountId") String accountId) {
        return new DataVO<>(accountService.changeAccountAvatar(accountId, in));
    }

    @Path("accounts/{accountId}/avatar")
    @GET
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getAccountAvatar(@PathParam("accountId") String accountId) {
        File avatarFile = accountService.getAccountAvatar(accountId);
        if (avatarFile != null) {
            return Response.ok(avatarFile)
                    .header("Content-disposition", "attachment;filename=account-avatar.png")
                    .header("Cache-Control", "no-cache")
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}

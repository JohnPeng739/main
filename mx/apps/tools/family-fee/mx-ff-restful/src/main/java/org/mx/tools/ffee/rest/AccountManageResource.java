package org.mx.tools.ffee.rest;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.ffee.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;

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

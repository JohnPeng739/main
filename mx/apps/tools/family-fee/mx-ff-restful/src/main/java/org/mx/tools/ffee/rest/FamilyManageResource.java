package org.mx.tools.ffee.rest;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.rest.vo.FamilyInfoVO;
import org.mx.tools.ffee.rest.vo.FamilyJoinInfoVO;
import org.mx.tools.ffee.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;

@Path("rest/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FamilyManageResource {
    private FamilyService familyService;
    private SessionDataStore sessionDataStore;

    @Autowired
    public FamilyManageResource(FamilyService familyService, SessionDataStore sessionDataStore) {
        super();
        this.familyService = familyService;
        this.sessionDataStore = sessionDataStore;
    }

    @Path("families/new")
    @POST
    public DataVO<Family> createFamily(FamilyInfoVO familyInfoVO) {
        if (familyInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        String userCode = familyInfoVO.getOpenId();
        sessionDataStore.setCurrentUserCode(userCode);
        Family family = familyInfoVO.get();
        family.setId(null);
        return new DataVO<>(familyService.createFamily(family, userCode, familyInfoVO.getOwnerRole()));
    }

    @Path("families/{familyId}/join")
    @PUT
    public DataVO<Family> joinFamily(@PathParam("familyId") String familyId,
                                     FamilyJoinInfoVO familyJoinInfoVO) {
        if (familyJoinInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(familyJoinInfoVO.getOpenId());
        return new DataVO<>(familyService.joinFamily(familyId, familyJoinInfoVO.getRole(),
                familyJoinInfoVO.getAccountId()));
    }

    @Path("families/{familyId}")
    @PUT
    public DataVO<Family> modifyFamily(@PathParam("familyId") String familyId,
                                       FamilyInfoVO familyInfoVO) {
        if (familyInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(familyInfoVO.getOpenId());
        Family family = familyInfoVO.get();
        family.setId(familyId);
        return new DataVO<>(familyService.modifyFamily(family));
    }

    @Path("families/{familyId}")
    @GET
    public DataVO<Family> getFamily(@PathParam("familyId") String familyId) {
        return new DataVO<>(familyService.getFamily(familyId));
    }

    @Path("families/{familyId}/avatar")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public DataVO<String> changeFamilyAvatar(@FormDataParam("file") InputStream in,
                                     @FormDataParam("file") FormDataContentDisposition detail,
                                     @PathParam("familyId") String familyId) {
        return new DataVO<>(familyService.changeFamilyAvatar(familyId, in));
    }

    @Path("families/{familyId}/avatar")
    @GET
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getFamilyAvatar(@PathParam("familyId") String familyId) {
        File avatarFile = familyService.getFamilyAvatar(familyId);
        if (avatarFile != null) {
            return Response.ok(avatarFile)
                    .header("Content-disposition", "attachment;filename=family-avatar.png")
                    .header("Cache-Control", "no-cache")
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("families/{familyId}/qrcode")
    @GET
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getFamilyQrCode(@PathParam("familyId") String familyId) {
        File qrCodeFile = familyService.getFamilyQrCode(familyId);
        if (qrCodeFile != null) {
            return Response.ok(qrCodeFile)
                    .header("Content-disposition", "attachment;filename=qrcode.png")
                    .header("Cache-Control", "no-cache")
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}

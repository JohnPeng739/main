package org.mx.tools.ffee.rest;

import org.mx.StringUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.rest.vo.FamilyInfoVO;
import org.mx.tools.ffee.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component("familyManageResource")
@Path("rest/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FamilyManageResource {
    private FamilyService familyService;

    @Autowired
    public FamilyManageResource(FamilyService familyService) {
        super();
        this.familyService = familyService;
    }

    @Path("families/new")
    @POST
    public DataVO<Family> createFamily(FamilyInfoVO familyInfoVO) {
        if (familyInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family family = familyInfoVO.get();
        return new DataVO<>(familyService.createFamily(family));
    }

    @Path("families/{familyId}")
    @PUT
    public DataVO<Family> modifyFamily(@PathParam("familyId") String familyId, FamilyInfoVO familyInfoVO) {
        if (familyInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family family = familyInfoVO.get();
        return new DataVO<>(familyService.modifyFamily(family));
    }

    @Path("families/{familyId}")
    @GET
    public DataVO<Family> getFamily(@PathParam("familyId") String familyId) {
        if (StringUtils.isBlank(familyId)) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        return new DataVO<>(familyService.getFamily(familyId));
    }

    @Path("families/{familyId}/join")
    @PUT
    public DataVO<Family> joinFamily(@PathParam("familyId") String familyId, @QueryParam("userCode") String userCode) {
        if (StringUtils.isBlank(familyId) || StringUtils.isBlank(userCode)) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        return new DataVO<>(familyService.joinFamily(familyId, userCode));
    }
}

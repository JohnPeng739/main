package org.mx.tools.ffee.rest;

import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.rest.vo.FamilyInfoVO;
import org.mx.tools.ffee.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    public DataVO<Family> createFamily(@QueryParam("userCode") String userCode,
                                       FamilyInfoVO familyInfoVO) {
        if (familyInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(userCode);
        Family family = familyInfoVO.get();
        family.setId(null);
        return new DataVO<>(familyService.saveFamily(family));
    }

    @Path("families/{familyId}/join")
    @PUT
    public DataVO<Family> joinFamily(@PathParam("familyId") String familyId,
                                     @QueryParam("userCode") String userCode) {
        sessionDataStore.setCurrentUserCode(userCode);
        return new DataVO<>(familyService.joinFamily(familyId, userCode));
    }

    @Path("families/{familyId}")
    @PUT
    public DataVO<Family> modifyFamily(@PathParam("familyId") String familyId,
                                       @QueryParam("userCode") String userCode,
                                       FamilyInfoVO familyInfoVO) {
        if (familyInfoVO == null) {
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        sessionDataStore.setCurrentUserCode(userCode);
        Family family = familyInfoVO.get();
        return new DataVO<>(familyService.saveFamily(family));
    }

    @Path("families/{familyId}")
    @GET
    public DataVO<Family> getFamily(@PathParam("familyId") String familyId) {
        return new DataVO<>(familyService.getFamily(familyId));
    }
}

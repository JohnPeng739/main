package org.mx.tools.ffee.rest;

import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceException;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.rest.vo.FamilyManageVO;
import org.mx.tools.ffee.rest.vo.FamilyVO;
import org.mx.tools.ffee.service.FamilyManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 描述： FFEE家庭管理Restful服务
 *
 * @author John.Peng
 *         Date time 2018/2/18 下午5:07
 */
@Component
@Path("rest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FfeeFamilyManageResource {
    @Autowired
    private FamilyManageService familyManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("family")
    @GET
    public DataVO<FamilyVO> getFamily(@QueryParam("fid") String id, @QueryParam("name") String name) {
        try {
            Family family = familyManageService.getFamily(id, name);
            return new DataVO<>(FamilyVO.transform(family));
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("family/create")
    @POST
    public DataVO<FamilyVO> createFamily(@QueryParam("userCode") String userCode, FamilyManageVO familyManageVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Family family = familyManageService.createFamily(familyManageVO.getName(),
                    familyManageVO.getFfeeAccountId(), familyManageVO.getMemberRole());
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(FamilyVO.transform(family));

        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("family/join")
    @POST
    public DataVO<FamilyVO> joinFamily(@QueryParam("userCode") String userCode, FamilyManageVO familyManageVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Family family = familyManageService.joinFamily(familyManageVO.getFamilyId(),
                    familyManageVO.getFfeeAccountId(), familyManageVO.getMemberRole());
            return new DataVO<>(FamilyVO.transform(family));
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        }
    }
}

package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.rest.vo.RoleInfoVO;
import org.mx.comps.rbac.rest.vo.RoleVO;
import org.mx.comps.rbac.service.RoleManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceException;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.rest.vo.DataVO;
import org.mx.rest.vo.PaginationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path("rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleManageResource {
    private static final Log logger = LogFactory.getLog(RoleManageResource.class);

    @Autowired
    private RoleManageService roleManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("roles")
    @GET
    public DataVO<List<RoleVO>> roles() {
        try {
            List<Role> roles = roleManageService.list(Role.class);
            List<RoleVO> vos = RoleVO.transformRoleVOs(roles);
            return new DataVO<>(vos);
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

    @Path("roles")
    @POST
    public PaginationDataVO<List<RoleVO>> roles(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<Role> roles = roleManageService.list(pagination, Role.class);
            List<RoleVO> vos = RoleVO.transformRoleVOs(roles);
            return new PaginationDataVO<>(pagination, vos);
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

    @Path("roles/{id}")
    @GET
    public DataVO<RoleVO> getRole(@PathParam("id") String id) {
        try {
            Role role = roleManageService.getById(id, Role.class);
            RoleVO vo = new RoleVO();
            RoleVO.transform(role, vo);
            return new DataVO<>(vo);
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

    @Path("roles/new")
    @POST
    public DataVO<RoleVO> saveRole(@QueryParam("userCode") String userCode, RoleInfoVO roleInfoVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            roleInfoVO.setRoleId(null);
            Role role = roleManageService.saveRole(roleInfoVO.getRoleInfo());
            RoleVO vo = new RoleVO();
            RoleVO.transform(role, vo);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(vo);
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

    @Path("roles/{id}")
    @PUT
    public DataVO<RoleVO> saveRole(@QueryParam("userCode") String userCode, @PathParam("id") String id, RoleInfoVO roleInfoVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            roleInfoVO.setRoleId(id);
            Role role = roleManageService.saveRole(roleInfoVO.getRoleInfo());
            RoleVO vo = new RoleVO();
            RoleVO.transform(role, vo);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(vo);
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

    @Path("roles/{id}")
    @DELETE
    public DataVO<RoleVO> deleteRole(@QueryParam("userCode") String userCode, @PathParam("id") String id) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Role role = roleManageService.remove(id, Role.class);
            RoleVO vo = new RoleVO();
            RoleVO.transform(role, vo);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(vo);
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

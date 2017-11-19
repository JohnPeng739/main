package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.Privilege;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
import org.mx.comps.rbac.error.UserInterfaceErrors;
import org.mx.comps.rbac.rest.vo.PrivilegeVO;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.session.SessionDataStore;
import org.mx.rest.vo.DataVO;
import org.mx.rest.vo.PaginationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path("rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PrivilegeManageResource {
    private static final Log logger = LogFactory.getLog(PrivilegeManageResource.class);

    @Autowired
    @Qualifier("generalDictEntityAccessorHibernate")
    private GeneralDictAccessor accessor = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("privileges")
    @GET
    public DataVO<List<PrivilegeVO>> privileges() {
        try {
            List<Privilege> privileges = accessor.list(Privilege.class);
            List<PrivilegeVO> vos = PrivilegeVO.transformPrivilegeVOs(privileges);
            return new DataVO<>(vos);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("privileges")
    @POST
    public PaginationDataVO<List<PrivilegeVO>> privileges(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<Privilege> privileges = accessor.list(pagination, Privilege.class);
            List<PrivilegeVO> vos = PrivilegeVO.transformPrivilegeVOs(privileges);
            return new PaginationDataVO<>(pagination, vos);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("privileges/{id}")
    @GET
    public DataVO<PrivilegeVO> getPrivilege(@PathParam("id") String id) {
        try {
            Privilege privilege = accessor.getById(id, Privilege.class);
            PrivilegeVO vo = new PrivilegeVO();
            PrivilegeVO.transform(privilege, vo);
            return new DataVO<>(vo);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    private DataVO<PrivilegeVO> savePrivilegeInfo(String userCode, PrivilegeVO privilegeVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Privilege privilege = EntityFactory.createEntity(Privilege.class);
            PrivilegeVO.transform(privilegeVO, privilege);
            privilege = accessor.save(privilege);
            PrivilegeVO vo = new PrivilegeVO();
            PrivilegeVO.transform(privilege, vo);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(vo);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        } catch (EntityInstantiationException ex) {
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.ENTITY_INSTANCE_FAIL));
        }
    }

    @Path("privileges/new")
    @POST
    public DataVO<PrivilegeVO> savePrivilege(@QueryParam("userCode") String userCode, PrivilegeVO privilegeVO) {
        return savePrivilegeInfo(userCode, privilegeVO);
    }

    @Path("privileges/{id}")
    @PUT
    public DataVO<PrivilegeVO> savePrivilege(@QueryParam("userCode") String userCode, @PathParam("id") String id, PrivilegeVO privilegeVO) {
        return savePrivilegeInfo(userCode, privilegeVO);
    }

    @Path("privilege/{id}")
    @DELETE
    public DataVO<PrivilegeVO> savePrivilege(@QueryParam("userCode") String userCode, @PathParam("id") String id) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            Privilege privilege = accessor.remove(id, Privilege.class);
            PrivilegeVO vo = new PrivilegeVO();
            PrivilegeVO.transform(privilege, vo);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(vo);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }
}

package org.mx.comps.rbac.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.dal.entity.User;
import org.mx.comps.rbac.error.UserInterfaceErrorException;
import org.mx.comps.rbac.error.UserInterfaceErrors;
import org.mx.comps.rbac.rest.vo.UserVO;
import org.mx.comps.rbac.service.UserManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.Pagination;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.dal.service.GeneralEntityAccessor;
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
@Path("/rest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserManageResource {
    private static final Log logger = LogFactory.getLog(UserManageResource.class);

    @Autowired
    @Qualifier("generalEntityAccessorHibernate")
    private GeneralEntityAccessor accessor = null;

    @Autowired
    private UserManageService userManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("users")
    @GET
    public DataVO<List<UserVO>> listUsers() {
        try {
            List<User> users = accessor.list(User.class);
            List<UserVO> userVOS = UserVO.transformUserVOs(users);
            return new DataVO<>(userVOS);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("users")
    @POST
    public PaginationDataVO<List<UserVO>> listUsersPagination(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<User> users = accessor.list(pagination, User.class);
            List<UserVO> userVOs = UserVO.transformUserVOs(users);
            return new PaginationDataVO<>(pagination, userVOs);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("users/{id}")
    @GET
    public DataVO<UserVO> getUser(@PathParam("id") String id) {
        try {
            User user = accessor.getById(id, User.class);
            UserVO userVO = new UserVO();
            UserVO.transform(user, userVO);
            return new DataVO<>(userVO);
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("user")
    @POST
    public DataVO<UserVO> saveUser(@QueryParam("userCode") String userCode, UserVO userVO) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            User user = EntityFactory.createEntity(User.class);
            UserVO.transform(userVO, user);
            user = accessor.save(user);
            UserVO.transform(user, userVO);
            return new DataVO<>(userVO);
        } catch (EntityInstantiationException | EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("user")
    @DELETE
    public DataVO<UserVO> deleteUser(@QueryParam("userCode") String userCode, @QueryParam("userId") String userId) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            User user = userManageService.deleteUser(userId);
            UserVO userVO = new UserVO();
            UserVO.transform(user, userVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(userVO);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }
}

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
import org.mx.dal.exception.EntityNotFoundException;
import org.mx.dal.session.SessionDataStore;
import org.mx.rest.vo.DataVO;
import org.mx.rest.vo.PaginationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserManageService userManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("users")
    @GET
    public DataVO<List<UserVO>> listUsers() {
        try {
            List<User> users = userManageService.list(User.class);
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
            List<User> users = userManageService.list(pagination, User.class);
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
            User user = userManageService.getById(id, User.class);
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
            user = userManageService.save(user);
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
            User user = userManageService.remove(userId, User.class);
            UserVO userVO = new UserVO();
            UserVO.transform(user, userVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(userVO);
        } catch (EntityNotFoundException ex) {
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.USER_NOT_FOUND));
        } catch (EntityAccessException ex) {
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }
}

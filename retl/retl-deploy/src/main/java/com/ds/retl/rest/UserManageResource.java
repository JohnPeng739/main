package com.ds.retl.rest;

import com.ds.retl.dal.entity.User;
import com.ds.retl.dal.entity.UserOperateLog;
import com.ds.retl.dal.exception.UserInterfaceErrorException;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.rest.vo.user.AuthenticateVO;
import com.ds.retl.rest.vo.user.ChangePasswordVO;
import com.ds.retl.rest.vo.user.OperateLogVO;
import com.ds.retl.rest.vo.user.UserVO;
import com.ds.retl.service.UserManageService;
import org.mx.StringUtils;
import org.mx.dal.Pagination;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.session.SessionDataStore;
import org.mx.rest.vo.DataVO;
import org.mx.rest.vo.PaginationDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2017/10/6.
 */

@Component
@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserManageResource {
    @Autowired
    @Qualifier("generalDictEntityAccessorHibernate")
    private GeneralDictAccessor accessor = null;

    @Autowired
    private UserManageService userManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("init")
    @GET
    public DataVO<UserVO> initUser() {
        try {
            sessionDataStore.setCurrentUserCode("SYSTEM");
            User user = userManageService.initUser();
            UserVO userVO = new UserVO();
            UserVO.transform(user, userVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(userVO);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("user/logs")
    @POST
    public PaginationDataVO<List<OperateLogVO>> listUserOperateLogs(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<UserOperateLog> logs = accessor.list(pagination, UserOperateLog.class);
            List<OperateLogVO> list = new ArrayList<>();
            if (logs != null && logs.size() > 0) {
                logs.forEach(log -> {
                    OperateLogVO vo = new OperateLogVO();
                    OperateLogVO.transform(log, vo);
                    list.add(vo);
                });
            }
            return new PaginationDataVO<>(pagination, list);
        } catch (EntityAccessException ex) {
            return new PaginationDataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("users")
    @POST
    public PaginationDataVO<List<UserVO>> listUsers(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<User> users = accessor.list(pagination, User.class);
            List<UserVO> list = new ArrayList<>();
            if (users != null && users.size() > 0) {
                users.forEach(user -> {
                    UserVO userVO = new UserVO();
                    UserVO.transform(user, userVO);
                    list.add(userVO);
                });
            }
            return new PaginationDataVO<>(pagination, list);
        } catch (EntityAccessException ex) {
            return new PaginationDataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("users/{userCode}")
    @GET
    public DataVO<UserVO> getUser(@PathParam("userCode") String userCode) {
        try {
            User user = accessor.getByCode(userCode, User.class);
            if (user == null) {
                return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.USER_NOT_FOUND));
            }
            UserVO userVO = new UserVO();
            UserVO.transform(user, userVO);
            return new DataVO<>(userVO);
        } catch (EntityAccessException ex) {
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("password/change")
    @POST
    public DataVO<Boolean> changePassword(ChangePasswordVO vo, @QueryParam("userCode") String userCode) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            userManageService.changePassword(userCode, vo.getOldPassword(), vo.getNewPassword());
            return new DataVO<>(true);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("login")
    @POST
    public DataVO<UserVO> login(AuthenticateVO login) {
        if (login == null) {
            return new DataVO<>();
        }
        sessionDataStore.setCurrentUserCode(login.getUser());
        try {
            User user = userManageService.login(login.getUser(), login.getPassword());
            UserVO userVO = new UserVO();
            UserVO.transform(user, userVO);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(userVO);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("logout")
    @POST
    public DataVO<Boolean> logout(@QueryParam("userCode") String userCode) {
        if (StringUtils.isBlank(userCode)) {
            return new DataVO<>(UserInterfaceErrors.USER_NOT_FOUND);
        }
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            userManageService.logout(userCode);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(true);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }
}

package com.ds.retl.rest;

import com.ds.retl.dal.entity.User;
import com.ds.retl.dal.entity.UserOperateLog;
import com.ds.retl.exception.UserInterfaceErrorException;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.rest.vo.user.AuthenticateVO;
import com.ds.retl.rest.vo.user.ChangePasswordVO;
import com.ds.retl.rest.vo.user.OperateLogVO;
import com.ds.retl.rest.vo.user.UserVO;
import com.ds.retl.service.UserManageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.Op;
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
 * 用户管理相关的RESTful服务资源类
 *
 * @author : john.peng created on date : 2017/10/6
 */
@Component
@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserManageResource {
    private static final Log logger = LogFactory.getLog(UserManageResource.class);

    @Autowired
    @Qualifier("generalDictEntityAccessorHibernate")
    private GeneralDictAccessor accessor = null;

    @Autowired
    private UserManageService userManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    /**
     * 初始化用户
     *
     * @return 初始化的管理员用户
     */
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
    @GET
    public DataVO<List<OperateLogVO>> listUserOperateLogs() {
        try {
            List<UserOperateLog> logs = accessor.list(UserOperateLog.class);
            return new DataVO<>(OperateLogVO.transform(logs));
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    /**
     * 按页列举用户操作日志
     *
     * @param pagination 分页对象
     * @return 符合条件的用户操作日志
     */
    @Path("user/logs")
    @POST
    public PaginationDataVO<List<OperateLogVO>> listUserOperateLogs(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<UserOperateLog> logs = accessor.list(pagination, UserOperateLog.class);
            return new PaginationDataVO<>(pagination, OperateLogVO.transform(logs));
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    @Path("users")
    @GET
    public DataVO<List<UserVO>> listUsers() {
        try {
            List<User> users = accessor.list(User.class);
            return new DataVO<>(UserVO.transform(users));
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    /**
     * 按页列举用户列表
     *
     * @param pagination 分页对象
     * @return 符合条件的用户列表
     */
    @Path("users")
    @POST
    public PaginationDataVO<List<UserVO>> listUsers(Pagination pagination) {
        if (pagination == null) {
            pagination = new Pagination();
        }
        try {
            List<User> users = accessor.list(pagination, User.class);
            return new PaginationDataVO<>(pagination, UserVO.transform(users));
        } catch (EntityAccessException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new PaginationDataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    /**
     * 获取指定用户代码的用户信息
     *
     * @param userCode 用户代码
     * @return 用户信息对象
     */
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
            if (logger.isErrorEnabled()) {
                logger.error(ex);
            }
            return new DataVO<>(new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL));
        }
    }

    /**
     * 修改用户密码
     *
     * @param vo       修改密码值对象
     * @param userCode 操作用户代码
     * @return 修改成功返回true，否则返回错误信息。
     */
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

    /**
     * 登入系统
     *
     * @param login 用户认证值对象
     * @return 登录成功返回登录用户信息对象，否则返回错误信息。
     */
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

    /**
     * 登出系统
     *
     * @param userCode 登出用户代码
     * @return 登出成功返回true，否则返回错误信息。
     */
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

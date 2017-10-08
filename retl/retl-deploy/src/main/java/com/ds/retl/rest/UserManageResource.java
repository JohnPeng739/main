package com.ds.retl.rest;

import com.ds.retl.dal.entity.User;
import com.ds.retl.dal.exception.UserInterfaceErrorException;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.rest.vo.user.AuthenticateVO;
import com.ds.retl.rest.vo.user.UserVO;
import com.ds.retl.service.UserManageService;
import net.bytebuddy.asm.Advice;
import org.mx.StringUtils;
import org.mx.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;

/**
 * Created by john on 2017/10/6.
 */

@Component
@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserManageResource {
    @Autowired
    private UserManageService userManageService = null;

    @Path("login")
    @POST
    public DataVO<UserVO> login(AuthenticateVO login) {
        if (login == null) {
            return new DataVO<>();
        }
        try {
            User user = userManageService.login(login.getUser(), login.getPassword());
            UserVO userVO = new UserVO();
            UserVO.transform(user, userVO);
            return new DataVO<>(userVO);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("logout")
    @POST
    public DataVO<Boolean> logout(@QueryParam("userCode")String user) {
        if (StringUtils.isBlank(user)) {
           return new DataVO<>(UserInterfaceErrors.USER_NOT_FOUND);
        }
        try {
            userManageService.logout(user);
            return new DataVO<>(true);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }
}

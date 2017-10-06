package com.ds.retl.rest;

import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.rest.vo.user.AuthenticateVO;
import com.ds.retl.rest.vo.user.UserVO;
import org.mx.StringUtils;
import org.mx.rest.vo.DataVO;
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
    @Path("login")
    @POST
    public DataVO<UserVO> login(AuthenticateVO login) {
        if (login == null) {
            return new DataVO<>();
        }
        // TODO 进行真正的登录操作
        UserVO user = new UserVO(login.getUser(), "John Peng");
        user.setTools(Arrays.asList("/summary", "/tasks/add"));
        user.setRoles(Arrays.asList("manager"));
        return new DataVO<>(user);
    }

    @Path("logout")
    @POST
    public DataVO<Boolean> logout(@QueryParam("userCode")String user) {
        if (StringUtils.isBlank(user)) {
           return new DataVO<>(UserInterfaceErrors.USER_NOT_FOUND);
        }
        // TODO 进行真正的登出操作
        return new DataVO<>(true);
    }
}

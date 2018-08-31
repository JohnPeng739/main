package org.mx.tools.elastic.restful;

import org.mx.dal.utils.ElasticLowLevelUtil;
import org.mx.dal.utils.bean.NodeInfoBean;
import org.mx.service.rest.auth.RestAuthenticate;
import org.mx.service.rest.vo.DataVO;
import org.mx.tools.elastic.restful.vo.AuthenticateVO;
import org.mx.tools.elastic.restful.vo.ChangePasswordVO;
import org.mx.tools.elastic.service.AuthenticateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path("rest/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SystemManageResource {
    private ElasticLowLevelUtil elasticLowLevelUtil;
    private AuthenticateService authenticateService;

    @Autowired
    public SystemManageResource(ElasticLowLevelUtil elasticLowLevelUtil, AuthenticateService authenticateService) {
        super();
        this.elasticLowLevelUtil = elasticLowLevelUtil;
        this.authenticateService = authenticateService;
    }

    @Path("login")
    @POST
    public DataVO<AuthenticateService.AccountBean> login(AuthenticateVO authenticateVO) {
        // 默认密码为：**#**12345
        return new DataVO<>(authenticateService.login(authenticateVO.getCode(), authenticateVO.getPassword()));
    }

    @RestAuthenticate
    @Path("password")
    @PUT
    public DataVO<AuthenticateService.AccountBean> changePassword(ChangePasswordVO changePasswordVO) {
        return new DataVO<>(authenticateService.changePassword(changePasswordVO.getCode(),
                changePasswordVO.getOldPassword(), changePasswordVO.getNewPassword()));
    }

    @RestAuthenticate
    @Path("nodes")
    @GET
    public DataVO<List<NodeInfoBean>> getAllNodes() {
        return new DataVO<>(elasticLowLevelUtil.getAllNodes());
    }
}

package com.ds.retl.rest;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.exception.UserInterfaceErrorException;
import com.ds.retl.rest.vo.server.LocalServerInfoVO;
import com.ds.retl.service.ServerManageService;
import org.mx.dal.session.SessionDataStore;
import org.mx.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务器管理配置相关的Restful服务资源
 *
 * @author : john.peng created on date : 2017/11/2
 */
@Component
@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerManageResource {
    @Autowired
    private ServerManageService serverManageService = null;

    @Autowired
    private SessionDataStore sessionDataStore = null;

    @Path("servers")
    @GET
    public DataVO<List<LocalServerInfoVO>> getServers() {
        try {
            Map<String, JSONObject> map = serverManageService.getServerConfigInfos();
            List<LocalServerInfoVO> list = new ArrayList<>();
            map.values().forEach(json -> list.add(new LocalServerInfoVO(json)));
            return new DataVO<>(list);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("server/systemctl")
    @GET
    public DataVO<Boolean> systemctl(@QueryParam("cmd") String cmd, @QueryParam("service") String service) {
        try {
            serverManageService.service(ServerManageService.ServiceType.SYSTEMCTL, cmd, service);
            return new DataVO<>(true);
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("server")
    @GET
    public DataVO<LocalServerInfoVO> getLocalServerInfo() {
        try {
            JSONObject json = serverManageService.getLocalServerConfigInfo();
            return new DataVO<>(new LocalServerInfoVO(json));
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }

    @Path("server")
    @POST
    public DataVO<LocalServerInfoVO> saveLocalServerInfo(@QueryParam("userCode") String userCode, String info) {
        sessionDataStore.setCurrentUserCode(userCode);
        try {
            JSONObject json = serverManageService.saveLocalServerConfigInfo(info);
            sessionDataStore.removeCurrentUserCode();
            return new DataVO<>(new LocalServerInfoVO(json));
        } catch (UserInterfaceErrorException ex) {
            return new DataVO<>(ex);
        }
    }
}

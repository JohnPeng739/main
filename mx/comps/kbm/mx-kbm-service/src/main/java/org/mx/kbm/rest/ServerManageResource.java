package org.mx.kbm.rest;

import org.mx.SystemUtils;
import org.mx.kbm.KbmApplication;
import org.mx.kbm.rest.vo.server.ServerStatVO;
import org.mx.service.rest.vo.DataVO;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 描述： 应用服务资源服务类，可以对服务器进行状态监控和关闭操作。
 *
 * @author john peng
 * Date time 2018/5/6 下午2:08
 */
@Component
@Path("rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerManageResource {
    @Path("servers/stop")
    @GET
    public DataVO<Boolean> stopServer() {
        KbmApplication.stopServer();
        return new DataVO<>(true);
    }

    @Path("servers/stat")
    @GET
    public DataVO<ServerStatVO> stat() {
        return new DataVO<>(getStat());
    }

    private ServerStatVO getStat() {
        ServerStatVO vo = new ServerStatVO();
        String pid = SystemUtils.getJvmPid();
        float cpuRate = SystemUtils.getCpuRate(KbmApplication.class.getName()),
                memoryRate = SystemUtils.getMemoryRate();
        int thread = SystemUtils.getThreads();
        vo.setPid(pid);
        vo.setCpuRate(cpuRate);
        vo.setMemoryRate(memoryRate);
        vo.setThreads(thread);
        return vo;
    }
}

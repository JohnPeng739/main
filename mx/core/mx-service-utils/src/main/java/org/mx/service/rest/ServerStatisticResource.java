package org.mx.service.rest;

import org.mx.SystemUtils;
import org.mx.service.rest.vo.DataVO;
import org.mx.service.rest.vo.ServerStatisticVO;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * 描述： 服务器控制的Restful服务资源
 *
 * @author John.Peng
 * Date time 2018/4/27 上午10:57
 */
@Path("rest/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerStatisticResource {

    @Path("server/statistic")
    @GET
    public DataVO<ServerStatisticVO> stat() {
        return new DataVO<>(getStat());
    }

    private ServerStatisticVO getStat() {
        ServerStatisticVO vo = new ServerStatisticVO();
        String pid = SystemUtils.getJvmPid();
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        float cpuRate = SystemUtils.getCpuRate(runtime.getName()),
                memoryRate = SystemUtils.getMemoryRate();
        int thread = SystemUtils.getThreads();
        vo.setPid(pid);
        vo.setCpuRate(cpuRate);
        vo.setMemoryRate(memoryRate);
        vo.setThreads(thread);
        return vo;
    }
}

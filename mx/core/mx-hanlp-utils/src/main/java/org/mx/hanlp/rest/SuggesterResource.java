package org.mx.hanlp.rest;

import org.mx.SystemUtils;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.hanlp.ItemSuggester;
import org.mx.hanlp.error.UserInterfaceHanlpErrorException;
import org.mx.hanlp.factory.SuggesterFactory;
import org.mx.hanlp.rest.vo.ServerStatVO;
import org.mx.hanlp.rest.vo.SuggestItemVO;
import org.mx.hanlp.rest.vo.SuggestRequestVO;
import org.mx.hanlp.server.SuggesterServer;
import org.mx.service.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * 描述： 提供推荐服务的监视、控制功能的Restful资源
 *
 * @author John.Peng
 *         Date time 2018/4/20 上午9:23
 */
@Component
@Path("rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SuggesterResource {
    @Autowired
    private SuggesterFactory suggesterFactory = null;

    @Path("servers/suggester/stop")
    @GET
    public DataVO<Boolean> stop() {
        return control("stop");
    }

    @Path("servers/suggester/stat")
    @GET
    public DataVO<ServerStatVO> stat() {
        return new DataVO<>(getStat());
    }

    @Path("suggest")
    @POST
    public DataVO<List<SuggestItemVO>> suggest(SuggestRequestVO suggestRequestVO) {
        ItemSuggester suggester = suggesterFactory.getSuggester(suggestRequestVO.getType());
        if (suggester == null) {
            return new DataVO<>(UserInterfaceHanlpErrorException.HanlpErrors.SUGGESTER_NOT_FOUND);
        }
        List<ItemSuggester.SuggestItem> items = suggester.suggest(suggestRequestVO.getKeyword(),
                suggestRequestVO.getSize());
        return new DataVO<>(SuggestItemVO.transform(items));
    }

    private ServerStatVO getStat() {
        ServerStatVO vo = new ServerStatVO();
        String pid = SystemUtils.getJvmPid();
        float cpuRate = SystemUtils.getCpuRate("org.mx.hanlp.server.SuggesterServer"),
                memoryRate = SystemUtils.getMemoryRate();
        int thread = SystemUtils.getThreads();
        vo.setPid(pid);
        vo.setCpuRate(cpuRate);
        vo.setMemoryRate(memoryRate);
        vo.setThreads(thread);
        return vo;
    }

    private DataVO<Boolean> control(String command) {
        switch (command) {
            case "stop":
                SuggesterServer.stopServer();
                break;
            default:
                return new DataVO<>(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        return new DataVO<>(true);
    }
}

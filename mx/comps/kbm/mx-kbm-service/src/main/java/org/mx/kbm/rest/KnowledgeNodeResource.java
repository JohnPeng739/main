package org.mx.kbm.rest;

import org.mx.kbm.rest.vo.NodeDetailsVO;
import org.mx.kbm.rest.vo.NodeShareRequestVO;
import org.mx.service.rest.vo.DataVO;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 描述： 知识条目服务资源类定义
 *
 * @author John.Peng
 *         Date time 2018/5/1 上午11:16
 */
@Component("knowledgeNodeResource")
@Path("rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KnowledgeNodeResource {
    @Path("nodes/{nodeId}")
    @GET
    public DataVO<NodeDetailsVO> getKnowledgeNodeDetails(@PathParam("nodeId") String nodeId) {
        // TODO get node details
        return null;
    }

    @Path("nodes/share")
    @POST
    public DataVO<Boolean> shareKnowledgeNode(NodeShareRequestVO nodeShareRequestVO) {
        // TODO share node
        return new DataVO<>(true);
    }
}

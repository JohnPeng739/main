package org.mx.kbm.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.jwt.AuthenticateAround;
import org.mx.dal.session.SessionDataStore;
import org.mx.error.UserInterfaceException;
import org.mx.kbm.Constants;
import org.mx.kbm.error.UserInterfaceKbmErrorException;
import org.mx.kbm.rest.vo.NodeDetailsVO;
import org.mx.kbm.rest.vo.NodeShareRequestVO;
import org.mx.kbm.service.NodeService;
import org.mx.service.rest.vo.DataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;

/**
 * 描述： 知识条目服务资源类定义
 *
 * @author John.Peng
 * Date time 2018/5/1 上午11:16
 */
@Component("knowledgeNodeResource")
@Path("rest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KnowledgeNodeResource {
    private static final Log logger = LogFactory.getLog(KnowledgeNodeResource.class);

    private SessionDataStore sessionDataStore;
    private NodeService nodeService;

    /**
     * 默认的构造函数
     *
     * @param sessionDataStore 会话数据服务接口
     * @param nodeService      知识条目服务接口
     */
    @Autowired
    public KnowledgeNodeResource(SessionDataStore sessionDataStore, NodeService nodeService) {
        super();
        this.sessionDataStore = sessionDataStore;
        this.nodeService = nodeService;
    }

    /**
     * 设置本模块中的session data
     *
     * @param userCode 当前操作用户代码
     */
    private void setSessionData(String userCode) {
        sessionDataStore.setCurrentSystem(Constants.SYSTEM);
        sessionDataStore.setCurrentModule(Constants.MODULE_KNOWLEDGE);
        sessionDataStore.setCurrentUserCode(userCode);
    }

    @Path("nodes/{nodeId}")
    @GET
    public DataVO<NodeDetailsVO> getKnowledgeNodeDetails(@PathParam("nodeId") String nodeId) {
        // TODO get node details
        return null;
    }

    @Path("nodes/share")
    @POST
    @AuthenticateAround(returnValueClass = DataVO.class)
    public DataVO<Boolean> shareKnowledgeNode(@QueryParam("userCode") String userCode,
                                              NodeShareRequestVO nodeShareRequestVO, @Context Request request) {
        setSessionData(userCode);
        try {
            nodeService.shareNode(nodeShareRequestVO.getNodeIds(), nodeShareRequestVO.getBeneficiaryIds(),
                    nodeShareRequestVO.getAcls(), nodeShareRequestVO.getStartTime(), nodeShareRequestVO.getEndTime());
            return new DataVO<>(true);
        } catch (UserInterfaceException ex) {
            return new DataVO<>(ex);
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Share the knowledge node fail.", ex);
            }
            return new DataVO<>(new UserInterfaceKbmErrorException(UserInterfaceKbmErrorException.KbmErrors.OTHER_FAIL));
        }
    }
}

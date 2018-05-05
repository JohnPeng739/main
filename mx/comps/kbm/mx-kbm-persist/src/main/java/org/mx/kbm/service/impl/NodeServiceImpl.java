package org.mx.kbm.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.kbm.entity.KnowledgeNode;
import org.mx.kbm.entity.KnowledgeSharedNode;
import org.mx.kbm.error.UserInterfaceKbmErrorException;
import org.mx.kbm.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述： 知识条目服务实现类
 *
 * @author john peng
 * Date time 2018/5/5 下午8:44
 */
@Component("nodeService")
public class NodeServiceImpl implements NodeService {
    private static final Log logger = LogFactory.getLog(NodeServiceImpl.class);

    private GeneralAccessor accessor;
    private OperateLogService operateLogService;

    /**
     * 默认的构造函数
     *
     * @param accessor          实体类操作器
     * @param operateLogService 操作日志服务接口
     */
    @Autowired
    public NodeServiceImpl(@Qualifier("generalAccessorJpa") GeneralAccessor accessor,
                           OperateLogService operateLogService) {
        super();
        this.accessor = accessor;
        this.operateLogService = operateLogService;
    }

    /**
     * {@inheritDoc}
     *
     * @see NodeService#shareNode(List, List, int, long, long)
     */
    @Override
    @Transactional
    public void shareNode(List<String> nodeIds, List<String> beneficiaryIds, int acls, long startTime, long endTime) {
        if (nodeIds == null || nodeIds.isEmpty() || beneficiaryIds == null || beneficiaryIds.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn(String.format("The share request parameters invalid, node ids: %s, beneficiary ids: %s",
                        nodeIds == null ? null : StringUtils.merge(nodeIds),
                        beneficiaryIds == null ? null : StringUtils.merge(beneficiaryIds)));
            }
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        if (startTime <= 0) {
            startTime = System.currentTimeMillis();
        }
        for (String nodeId : nodeIds) {
            KnowledgeNode node = accessor.getById(nodeId, KnowledgeNode.class);
            if (node == null) {
                throw new UserInterfaceKbmErrorException(UserInterfaceKbmErrorException.KbmErrors.NODE_NOT_FOUND);
            }
            for (String beneficiaryId : beneficiaryIds) {
                Account beneficiary = accessor.getById(beneficiaryId, Account.class);
                if (beneficiary == null) {
                    throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
                }
                KnowledgeSharedNode sharedNode = EntityFactory.createEntity(KnowledgeSharedNode.class);
                sharedNode.setNode(node);
                sharedNode.setBeneficiary(beneficiary);
                sharedNode.setAcls(acls);
                sharedNode.setState(KnowledgeSharedNode.SharedState.SHARED);
                sharedNode.setStartTime(startTime);
                sharedNode.setEndTime(endTime);
                accessor.save(sharedNode);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Share the knowledge node successfully, node ids: %s, beneficiary ids: %s," +
                            "acls: %s, start time: %d, end time: %d", StringUtils.merge(nodeIds),
                    StringUtils.merge(beneficiaryIds), acls, startTime, endTime));
        }
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("分享[%d]个知识条目成功。", nodeIds.size()));
        }
    }
}

package org.mx.kbm.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.entity.OperateLog;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.kbm.entity.KnowledgeContact;
import org.mx.kbm.entity.KnowledgeTenant;
import org.mx.kbm.error.UserInterfaceKbmErrorException;
import org.mx.kbm.service.ContactService;
import org.mx.kbm.service.TenantService;
import org.mx.kbm.service.bean.TenantRegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 描述： 知识租户服务实现类
 *
 * @author John.Peng
 *         Date time 2018/4/29 下午9:32
 */
@Component("tenantService")
public class TenantServiceImpl implements TenantService {
    private static final Log logger = LogFactory.getLog(TenantServiceImpl.class);

    @Autowired
    @Qualifier("generalDictAccessorJpa")
    private GeneralDictAccessor dictAccessor = null;

    @Autowired
    private OperateLogService operateLogService = null;

    @Autowired
    private ContactService contactService = null;

    /**
     * {@inheritDoc}
     *
     * @see TenantService#register(TenantRegisterRequest)
     */
    @Transactional
    @Override
    public KnowledgeTenant register(TenantRegisterRequest registerRequest) {
        // 输入参数合法性检测
        if (registerRequest == null || StringUtils.isBlank(registerRequest.getCode())) {
            if (logger.isErrorEnabled()) {
                logger.error("The register request's parameter invalid.");
            }
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        KnowledgeTenant tenant = dictAccessor.getByCode(registerRequest.getCode(), KnowledgeTenant.class);
        if (tenant != null) {
            throw new UserInterfaceKbmErrorException(UserInterfaceKbmErrorException.KbmErrors.TENANT_HAS_EXISTED);
        }

        // 先注册联系人，并且不记录日志
        KnowledgeContact contact = contactService.register(registerRequest.getContact(), false);

        // 保存租户
        tenant = EntityFactory.createEntity(KnowledgeTenant.class);
        tenant.setContact(contact);
        tenant.setCode(registerRequest.getCode());
        tenant.setType(registerRequest.getType());
        tenant.setName(registerRequest.getName());
        tenant.setDesc(registerRequest.getDesc());
        tenant = dictAccessor.save(tenant);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Save a tenant[%s] successfully.", tenant.getId()));
        }

        // 记录操作日志
        if (operateLogService != null) {
            operateLogService.writeLog(OperateLog.OperateType.CRUD,
                    String.format("注册租户成功，代码：%s，名称：%s，联系人代码：%s，联系人名称：%s。", tenant.getCode(),
                            tenant.getName(), contact.getAccount().getCode(), contact.getAccount().getName()));
        }

        return tenant;
    }
}

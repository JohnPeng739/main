package org.mx.kbm.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.service.GeneralDictAccessor;
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
    @Qualifier("generalDictAccessor")
    private GeneralDictAccessor dictAccessor = null;

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
        if (registerRequest == null || StringUtils.isBlank(registerRequest.getCode()) ||
                registerRequest.getContact() == null || StringUtils.isBlank(registerRequest.getContact().getCode())) {
            if (logger.isErrorEnabled()) {
                logger.error("The register request's parameter invalid.");
            }
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        KnowledgeTenant tenant = dictAccessor.getByCode(registerRequest.getCode(), KnowledgeTenant.class);
        if (tenant != null) {
            throw new UserInterfaceKbmErrorException(UserInterfaceKbmErrorException.KbmErrors.TENANT_HAS_EXISTED);
        }
        KnowledgeContact contact = contactService.getContactByCode(registerRequest.getContact().getCode());
        if (contact != null) {
            throw new UserInterfaceKbmErrorException(UserInterfaceKbmErrorException.KbmErrors.CONTACT_HAS_EXISTED);
        }
        // 保存Account

        // 保存联系人

        // 保存租户
        return null;
    }
}

package org.mx.kbm.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.DigestUtils;
import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.dal.EntityFactory;
import org.mx.dal.entity.OperateLog;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.kbm.entity.KnowledgeContact;
import org.mx.kbm.entity.KnowledgeTenant;
import org.mx.kbm.error.UserInterfaceKbmErrorException;
import org.mx.kbm.respository.ContactRepository;
import org.mx.kbm.respository.TenantRepository;
import org.mx.kbm.service.ContactService;
import org.mx.kbm.service.bean.ContactDetailsBean;
import org.mx.kbm.service.bean.ContactRegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述： 基于Hibernate实现的联系人服务类
 *
 * @author John.Peng
 * Date time 2018/4/29 下午10:58
 */
@Component("contactService")
public class ContactServiceImpl implements ContactService {
    private static final Log logger = LogFactory.getLog(ContactServiceImpl.class);

    private GeneralDictAccessor dictAccessor;
    private OperateLogService operateLogService;
    private ContactRepository contactRepository;
    private TenantRepository tenantRepository;

    /**
     * 默认的构造函数
     *
     * @param dictAccessor      字段类型实体操作器
     * @param contactRepository 联系服务接口
     * @param tenantRepository  租户服务接口
     * @param operateLogService 操作日志服务接口
     */
    @Autowired
    public ContactServiceImpl(@Qualifier("generalDictAccessorJpa") GeneralDictAccessor dictAccessor,
                              ContactRepository contactRepository, TenantRepository tenantRepository,
                              OperateLogService operateLogService) {
        super();
        this.dictAccessor = dictAccessor;
        this.contactRepository = contactRepository;
        this.tenantRepository = tenantRepository;
        this.operateLogService = operateLogService;
    }

    /**
     * {@inheritDoc}
     *
     * @see ContactService#getContactByCode(String)
     */
    @Transactional(readOnly = true)
    @Override
    public KnowledgeContact getContactByCode(String code) {
        return contactRepository.findByCode(code);
    }

    /**
     * {@inheritDoc}
     *
     * @see ContactService#getContactDetailsById(String)
     */
    @Transactional(readOnly = true)
    @Override
    public ContactDetailsBean getContactDetailsById(String id) {
        KnowledgeContact contact = dictAccessor.getById(id, KnowledgeContact.class);
        if (contact == null) {
            throw new UserInterfaceKbmErrorException(UserInterfaceKbmErrorException.KbmErrors.CONTACT_NOT_FOUND);
        }
        ContactDetailsBean contactDetailsBean = new ContactDetailsBean();
        contactDetailsBean.setContact(contact);
        KnowledgeTenant managed = tenantRepository.getManagedTenantByContactId(contact.getId());
        contactDetailsBean.setManagedTenant(managed);
        List<? extends KnowledgeTenant> belongs = tenantRepository.getBelonesTenantByContactId(contact.getId());
        if (belongs != null && !belongs.isEmpty()) {
            // 如果返回列表包含有效数据，返回第一个租户
            contactDetailsBean.setBelongsTenant(belongs.get(0));
        }
        return contactDetailsBean;
    }

    /**
     * {@inheritDoc}
     *
     * @see ContactService#register(ContactRegisterRequest)
     * @see ContactService#register(ContactRegisterRequest, boolean)
     */
    @Transactional
    @Override
    public KnowledgeContact register(ContactRegisterRequest registerRequest) {
        return register(registerRequest, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see ContactService#register(ContactRegisterRequest, boolean)
     */
    @Transactional
    @Override
    public KnowledgeContact register(ContactRegisterRequest registerRequest, boolean needWriteLog) {
        // 输入参数合法性检测
        if (registerRequest == null || StringUtils.isBlank(registerRequest.getCode())) {
            if (logger.isErrorEnabled()) {
                logger.error("The register request's parameter invalid.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        KnowledgeTenant tenant = dictAccessor.getByCode(registerRequest.getCode(), KnowledgeTenant.class);
        if (tenant != null) {
            throw new UserInterfaceKbmErrorException(UserInterfaceKbmErrorException.KbmErrors.TENANT_HAS_EXISTED);
        }
        KnowledgeContact contact = this.getContactByCode(registerRequest.getCode());
        if (contact != null) {
            throw new UserInterfaceKbmErrorException(UserInterfaceKbmErrorException.KbmErrors.CONTACT_HAS_EXISTED);
        }

        // 保存Account
        Account account = EntityFactory.createEntity(Account.class);
        account.setCode(registerRequest.getCode());
        account.setName(registerRequest.getName());
        account.setPassword(DigestUtils.md5(registerRequest.getPassword()));
        account.setDesc(registerRequest.getDesc());
        account = dictAccessor.save(account);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Save a account[%s] successfully.", account.getId()));
        }

        // 保存联系人
        contact = EntityFactory.createEntity(KnowledgeContact.class);
        contact.setMobile(registerRequest.getMobile());
        contact.setEmail(registerRequest.getEmail());
        contact.setAddress(registerRequest.getAddress());
        contact.setAccount(account);
        contact = dictAccessor.save(contact);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Save a contact[%s] successfully.", contact.getId()));
        }

        // 记录日志
        if (needWriteLog && operateLogService != null) {
            operateLogService.writeLog(OperateLog.OperateType.CRUD,
                    String.format("注册联系人成功，代码：%s，名称：%s。", contact.getAccount().getCode(),
                            contact.getAccount().getName()));
        }

        return contact;
    }
}

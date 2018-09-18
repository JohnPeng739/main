package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.dal.entity.FamilyMember;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.service.FamilyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("familyService")
public class FamilyServiceImpl implements FamilyService {
    private static final Log logger = LogFactory.getLog(FamilyServiceImpl.class);

    private GeneralAccessor generalAccessor;

    @Autowired
    public FamilyServiceImpl(@Qualifier("generalAccessor") GeneralAccessor generalAccessor) {
        super();
        this.generalAccessor = generalAccessor;
    }

    @Transactional
    @Override
    public Family saveFamily(Family family) {
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error("The family is null.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        if (!StringUtils.isBlank(family.getId())) {
            Family checked = generalAccessor.getById(family.getId(), Family.class);
            if (checked != null) {
                // 修改家庭
                checked.setAvatarUrl(family.getAvatarUrl());
                checked.setDescription(family.getDescription());
                checked.setName(family.getName());
                family = checked;
            } else {
                // 新增家庭
                family.setId(null);
            }
        }
        family = generalAccessor.save(family);
        if (logger.isDebugEnabled()) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Save family successfully, id: %s, name: %s.",
                        family.getId(), family.getName()));
            }
        }
        return family;
    }

    @Transactional(readOnly = true)
    @Override
    public Family getFamily(String familyId) {
        if (StringUtils.isBlank(familyId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        return generalAccessor.getById(familyId, Family.class);
    }

    @Transactional
    @Override
    public Family joinFamily(String familyId, String openId) {
        if (StringUtils.isBlank(familyId) || StringUtils.isBlank(openId)) {
            if (logger.isErrorEnabled()) {
                logger.error("The family's id or open id is blank.");
            }
            throw new UserInterfaceSystemErrorException(
                    UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM
            );
        }
        Family family = generalAccessor.getById(familyId, Family.class);
        if (family == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The family[%s] not found.", familyId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED
            );
        }
        FfeeAccount account = generalAccessor.findOne(
                GeneralAccessor.ConditionTuple.eq("openId", openId), FfeeAccount.class);
        if (account == null) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("The account not found, open id: %s.", openId));
            }
            throw new UserInterfaceFfeeErrorException(
                    UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED
            );
        }
        for (FamilyMember member : family.getMembers()) {
            if (member.getFfeeAccount().getId().equals(account.getId())) {
                // 已经是家庭成员了，忽略
                return family;
            }
        }
        FamilyMember member = EntityFactory.createEntity(FamilyMember.class);
        member.setFamily(family);
        member.setFfeeAccount(account);
        member.setIsOwner(false);
        generalAccessor.save(member);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("The account[%s] join the family[%s] successfully.", openId, familyId));
        }
        return generalAccessor.getById(familyId, Family.class);
    }
}

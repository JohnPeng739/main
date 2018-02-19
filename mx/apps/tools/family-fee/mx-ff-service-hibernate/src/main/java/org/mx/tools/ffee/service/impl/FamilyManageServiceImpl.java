package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.error.UserInterfaceSystemErrorException;
import org.mx.tools.ffee.dal.entity.Family;
import org.mx.tools.ffee.dal.entity.FamilyMember;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.error.UserInterfaceFfeeErrorException;
import org.mx.tools.ffee.service.FamilyManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 描述： 基于Hibernate实现的家庭管理服务。
 *
 * @author John.Peng
 *         Date time 2018/2/18 下午8:13
 */
@Component
public class FamilyManageServiceImpl implements FamilyManageService {
    private static final Log logger = LogFactory.getLog(FamilyManageServiceImpl.class);

    @Autowired
    @Qualifier("generalAccessor")
    private GeneralAccessor accessor = null;

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * {@inheritDoc}
     *
     * @see FamilyManageService#getFamily(String, String)
     */
    @Transactional(readOnly = true)
    public Family getFamily(String id, String name) {
        if (StringUtils.isBlank(id) && StringUtils.isBlank(name)) {
            if (logger.isDebugEnabled()) {
                logger.debug("The family's id and name is blank.");
            }
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        } else if (!StringUtils.isBlank(id)) {
            return accessor.getById(id, Family.class);
        } else {
            List<GeneralAccessor.ConditionTuple> tuples = new ArrayList<>();
            tuples.add(new GeneralAccessor.ConditionTuple("name", name));
            List<Family> families = accessor.find(tuples, Family.class);
            return families == null || families.isEmpty() ? null : families.get(0);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see FamilyManageService#createFamily(String, String, String)
     */
    @Transactional()
    public Family createFamily(String name, String ffeeAccountId, String memberRole) {
        Family family = getFamily(null, name);
        if (family != null) {
            throw new UserInterfaceFfeeErrorException(UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_EXISTED);
        }
        FfeeAccount ffeeAccount = accessor.getById(ffeeAccountId, FfeeAccount.class);
        if (ffeeAccount == null) {
            throw new UserInterfaceFfeeErrorException(UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED);
        }
        FamilyMember member = EntityFactory.createEntity(FamilyMember.class);
        member.setFfeeAccount(ffeeAccount);
        member.setMemberRole(memberRole);
        member = accessor.save(member, false);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Create a new family member, account: %s, family: %s.",
                    ffeeAccount.getAccount().getName(), name));
        }
        family = EntityFactory.createEntity(Family.class);
        family.setName(name);
        family.setOwner(ffeeAccount);
        family.getMembers().add(member);
        family = accessor.save(family, false);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("成功创建家庭[%s]，家主是账户[%s]。", family.getName(),
                    ffeeAccount.getAccount().getName()));
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Create the family[%s] successfully, account: %s.",
                    family.getName(), ffeeAccount.getAccount().getName()));
        }
        return family;
    }

    /**
     * {@inheritDoc}
     *
     * @see FamilyManageService#joinFamily(String, String, String)
     */
    @Transactional()
    public Family joinFamily(String familyId, String ffeeAccountId, String memberRole) {
        Family family = getFamily(familyId, null);
        if (family == null) {
            throw new UserInterfaceFfeeErrorException(UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_NOT_EXISTED);
        }
        FfeeAccount ffeeAccount = accessor.getById(ffeeAccountId, FfeeAccount.class);
        if (ffeeAccount == null) {
            throw new UserInterfaceFfeeErrorException(UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_NOT_EXISTED);
        }
        Set<FamilyMember> members = family.getMembers();
        while (members != null && members.iterator().hasNext()) {
            FamilyMember member = members.iterator().next();
            if (member != null && member.getFfeeAccount() != null && ffeeAccountId.equals(member.getFfeeAccount().getId())) {
                throw new UserInterfaceFfeeErrorException(UserInterfaceFfeeErrorException.FfeeErrors.ACCOUNT_IN_MEMBERS);
            }
        }
        FamilyMember member = EntityFactory.createEntity(FamilyMember.class);
        member.setFfeeAccount(ffeeAccount);
        member.setMemberRole(memberRole);
        member = accessor.save(member, false);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Create a new family member, account: %s, family: %s.",
                    ffeeAccount.getAccount().getName(), family.getName()));
        }
        if (members == null) {
            throw new UserInterfaceFfeeErrorException(UserInterfaceFfeeErrorException.FfeeErrors.FAMILY_MEMBER_SAVE_FAIL);
        }
        members.add(member);
        family = accessor.save(family, false);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("账户[%s]成功加入家庭[%s]。", ffeeAccount.getAccount().getName(),
                    family.getName()));
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Account[%s] join the family[%s] successfully.",
                    ffeeAccount.getAccount().getName(), family.getName()));
        }
        return family;
    }
}

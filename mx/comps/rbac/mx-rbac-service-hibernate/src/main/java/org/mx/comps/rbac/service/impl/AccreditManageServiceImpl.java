package org.mx.comps.rbac.service.impl;

import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Accredit;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.AccreditManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.service.impl.GeneralEntityAccessorImpl;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 基于Hibernate JPA实现的授权管理服务实现。
 *
 * @author : john.peng created on date : 2017/12/01
 */
@Component("accreditManageService")
public class AccreditManageServiceImpl extends GeneralEntityAccessorImpl implements AccreditManageService {
    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * 判断指定的授权是否在有效授权中存在
     *
     * @param accreditInfo 授权信息对象
     * @return 如果已经存在，则返回true；否则返回false。
     */
    private boolean hasSameAccredit(AccreditInfo accreditInfo) {
        List<ConditionTuple> conditions = new ArrayList<>();
        conditions.add(new ConditionTuple("src.id", accreditInfo.getSrcAccountId()));
        conditions.add(new ConditionTuple("tar.id", accreditInfo.getTarAccountId()));
        conditions.add(new ConditionTuple("valid", true));
        List<Accredit> list = super.find(conditions, Accredit.class);
        List<Accredit> accredits = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            list.forEach(accredit -> {
                if (!accredit.isClosed()) {
                    accredits.add(accredit);
                }
            });
        }
        if (accredits.isEmpty()) {
            return false;
        }
        for (Accredit accredit : accredits) {
            if (!accredit.isClosed()) {
                for (String roleId : accreditInfo.getRoleIds()) {
                    boolean found = false;
                    for (Role role : accredit.getRoles()) {
                        if (roleId.equals(role.getId())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see AccreditManageService#accredit(AccreditInfo)
     */
    @Override
    @Transactional
    public Accredit accredit(AccreditInfo accreditInfo) {
        if (accreditInfo == null || StringUtils.isBlank(accreditInfo.getSrcAccountId()) ||
                StringUtils.isBlank(accreditInfo.getTarAccountId()) || accreditInfo.getRoleIds() == null ||
                accreditInfo.getRoleIds().isEmpty()) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        // 判断是否存在相同的有效授权
        if (hasSameAccredit(accreditInfo)) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCREDIT_SAME_FOUND);
        }
        Account src = super.getById(accreditInfo.getSrcAccountId(), Account.class);
        if (src == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
        }
        Account tar = super.getById(accreditInfo.getTarAccountId(), Account.class);
        if (tar == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
        }
        Set<Role> roles = new HashSet<>();
        for (String roleId : accreditInfo.getRoleIds()) {
            Role role = super.getById(roleId, Role.class);
            if (role == null) {
                throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ROLE_NOT_FOUND);
            }
            roles.add(role);
        }
        Accredit accredit = EntityFactory.createEntity(Accredit.class);
        accredit.setSrc(src);
        accredit.setTar(tar);
        accredit.setRoles(roles);
        accredit.setStartTime(new Date(accreditInfo.getStartTime()));
        if (accreditInfo.getEndTime() > 0 && accreditInfo.getEndTime() > accreditInfo.getStartTime()) {
            accredit.setEndTime(new Date(accreditInfo.getEndTime()));
        }
        accredit.setValid(true);
        accredit.setDesc(accreditInfo.getDesc());
        accredit = super.save(accredit, false);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("新增授权[%s=>%s]成功。", accredit.getSrc().getName(),
                    accredit.getTar().getName()));
        }
        return accredit;
    }

    /**
     * {@inheritDoc}
     *
     * @see AccreditManageService#closeAccredit(String)
     */
    @Override
    @Transactional
    public Accredit closeAccredit(String accreditId) {
        if (StringUtils.isBlank(accreditId)) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        Accredit accredit = super.getById(accreditId, Accredit.class);
        if (accredit == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCREDIT_NOT_FOUND);
        }
        if (accredit.isClosed()) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCREDIT_HAS_CLOSED);
        }
        accredit.setValid(false);
        accredit = super.save(accredit, false);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("关闭授权[%s=>%s]成功。", accredit.getSrc().getName(),
                    accredit.getTar().getName()));
        }
        return accredit;
    }
}

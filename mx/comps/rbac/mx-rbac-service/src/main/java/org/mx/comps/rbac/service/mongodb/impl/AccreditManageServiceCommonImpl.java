package org.mx.comps.rbac.service.mongodb.impl;

import org.mx.StringUtils;
import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Accredit;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.comps.rbac.service.AccreditManageService;
import org.mx.dal.EntityFactory;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.error.UserInterfaceSystemErrorException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 基于Hibernate JPA实现的授权管理服务实现。
 *
 * @author : john.peng created on date : 2017/12/01
 */
public abstract class AccreditManageServiceCommonImpl implements AccreditManageService {
    protected GeneralAccessor accessor = null;

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * 保存授权实体对象
     *
     * @param accredit 授权实体对象
     * @return 保存后的授权实体对象
     */
    protected abstract Accredit save(Accredit accredit);

    /**
     * 判断指定的授权是否在有效授权中存在
     *
     * @param accreditInfo 授权信息对象
     * @return 如果已经存在，则返回true；否则返回false。
     */
    protected abstract boolean hasSameAccredit(AccreditInfo accreditInfo);

    /**
     * {@inheritDoc}
     *
     * @see AccreditManageService#accredit(AccreditInfo)
     */
    @Override
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
        Account src = accessor.getById(accreditInfo.getSrcAccountId(), Account.class);
        if (src == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
        }
        Account tar = accessor.getById(accreditInfo.getTarAccountId(), Account.class);
        if (tar == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCOUNT_NOT_FOUND);
        }
        Set<Role> roles = new HashSet<>();
        for (String roleId : accreditInfo.getRoleIds()) {
            Role role = accessor.getById(roleId, Role.class);
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
        accredit = this.save(accredit);
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
    public Accredit closeAccredit(String accreditId) {
        if (StringUtils.isBlank(accreditId)) {
            throw new UserInterfaceSystemErrorException(UserInterfaceSystemErrorException.SystemErrors.SYSTEM_ILLEGAL_PARAM);
        }
        Accredit accredit = accessor.getById(accreditId, Accredit.class);
        if (accredit == null) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCREDIT_NOT_FOUND);
        }
        if (accredit.isClosed()) {
            throw new UserInterfaceRbacErrorException(UserInterfaceRbacErrorException.RbacErrors.ACCREDIT_HAS_CLOSED);
        }
        accredit.setValid(false);
        accredit = this.save(accredit);
        if (operateLogService != null) {
            operateLogService.writeLog(String.format("关闭授权[%s=>%s]成功。", accredit.getSrc().getName(),
                    accredit.getTar().getName()));
        }
        return accredit;
    }
}

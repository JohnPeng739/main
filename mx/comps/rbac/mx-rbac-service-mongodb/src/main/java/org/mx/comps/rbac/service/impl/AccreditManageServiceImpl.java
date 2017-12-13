package org.mx.comps.rbac.service.impl;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.comps.rbac.dal.entity.Accredit;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.service.AccreditManageService;
import org.mx.dal.service.GeneralAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于Hibernate JPA实现的授权管理服务实现。
 *
 * @author : john.peng created on date : 2017/12/01
 */
@Component("accreditManageService")
public class AccreditManageServiceImpl extends AccreditManageServiceCommonImpl {
    @Autowired
    @Qualifier("generalAccessor")
    private GeneralAccessor accessor = null;

    /**
     * {@inheritDoc}
     *
     * @see AccreditManageServiceCommonImpl#save(Accredit)
     */
    @Override
    protected Accredit save(Accredit accredit) {
        return accessor.save(accredit, false);
    }

    /**
     * {@inheritDoc}
     *
     * @see AccreditManageServiceCommonImpl#hasSameAccredit(AccreditInfo)
     */
    @Override
    protected boolean hasSameAccredit(AccreditInfo accreditInfo) {
        List<GeneralAccessor.ConditionTuple> conditions = new ArrayList<>();
        conditions.add(new GeneralAccessor.ConditionTuple("src", accessor.getById(accreditInfo.getSrcAccountId(), Account.class)));
        conditions.add(new GeneralAccessor.ConditionTuple("tar", accessor.getById(accreditInfo.getTarAccountId(), Account.class)));
        conditions.add(new GeneralAccessor.ConditionTuple("valid", true));
        List<Accredit> list = accessor.find(conditions, Accredit.class);
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
    public Accredit accredit(AccreditInfo accreditInfo) {
        super.accessor = accessor;
        return super.accredit(accreditInfo);
    }

    /**
     * {@inheritDoc}
     *
     * @see AccreditManageService#closeAccredit(String)
     */
    @Override
    public Accredit closeAccredit(String accreditId) {
        super.accessor = accessor;
        return super.closeAccredit(accreditId);
    }
}

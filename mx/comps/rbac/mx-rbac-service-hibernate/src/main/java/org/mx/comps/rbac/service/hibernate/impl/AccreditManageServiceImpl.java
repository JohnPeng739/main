package org.mx.comps.rbac.service.hibernate.impl;

import org.mx.comps.rbac.dal.entity.Accredit;
import org.mx.comps.rbac.dal.entity.Role;
import org.mx.comps.rbac.service.AccreditManageService;
import org.mx.comps.rbac.service.impl.AccreditManageServiceCommonImpl;
import org.mx.dal.service.GeneralAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于Hibernate JPA实现的授权管理服务实现。
 *
 * @author : john.peng created on date : 2017/12/01
 */
@Component("accreditManageServiceHibernate")
public class AccreditManageServiceImpl extends AccreditManageServiceCommonImpl {
    private GeneralAccessor accessor;

    /**
     * 默认的构造函数
     *
     * @param accessor 数据实体访问器
     */
    @Autowired
    public AccreditManageServiceImpl(@Qualifier("generalDictAccessor") GeneralAccessor accessor) {
        super();
        this.accessor = accessor;
    }

    /**
     * {@inheritDoc}
     *
     * @see AccreditManageServiceCommonImpl#save(Accredit)
     */
    @Override
    protected Accredit save(Accredit accredit) {
        return accessor.save(accredit);
    }

    /**
     * {@inheritDoc}
     *
     * @see AccreditManageServiceCommonImpl#hasSameAccredit(AccreditManageService.AccreditInfo)
     */
    @Override
    public boolean hasSameAccredit(AccreditManageService.AccreditInfo accreditInfo) {
        List<Accredit> accredits = accessor.find(GeneralAccessor.ConditionGroup.and(
                GeneralAccessor.ConditionTuple.eq("src.id", accreditInfo.getSrcAccountId()),
                GeneralAccessor.ConditionTuple.eq("tar.id", accreditInfo.getTarAccountId()),
                GeneralAccessor.ConditionTuple.eq("valid", true)
        ), Accredit.class)
                .stream()
                .filter(accredit -> !accredit.isClosed())
                .collect(Collectors.toList());
        if (accredits.isEmpty()) {
            return false;
        }
        for (Accredit accredit : accredits) {
            for (Role role : accredit.getRoles()) {
                if (accreditInfo.getRoleIds().contains(role.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see AccreditManageService#accredit(AccreditInfo)
     */
    @Override
    @Transactional
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
    @Transactional
    public Accredit closeAccredit(String accreditId) {
        super.accessor = accessor;
        return super.closeAccredit(accreditId);
    }
}

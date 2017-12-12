package org.mx.comps.rbac.service.impl;

import org.mx.comps.rbac.dal.entity.Accredit;
import org.mx.comps.rbac.service.AccreditManageService;
import org.mx.dal.service.GeneralAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于Hibernate JPA实现的授权管理服务实现。
 *
 * @author : john.peng created on date : 2017/12/01
 */
@Component("accreditManageService")
public class AccreditManageServiceImpl extends AccreditManageServiceCommonImpl {
    @Autowired
    @Qualifier("generalEntityAccessorHibernate")
    private GeneralAccessor accessor = null;

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

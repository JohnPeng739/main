package org.mx.tools.ffee.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.comps.rbac.service.AccountManageService;
import org.mx.dal.service.GeneralAccessor;
import org.mx.tools.ffee.dal.entity.FfeeAccount;
import org.mx.tools.ffee.service.FfeeAccountManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 描述： FFEE账户管理服务实现类，基于Hibernate实现。
 *
 * @author John.Peng
 *         Date time 2018/2/19 上午11:38
 */
@Component
public class FfeeAccountManageServiceImpl implements FfeeAccountManageService {
    private static final Log logger = LogFactory.getLog(FfeeAccountManageServiceImpl.class);

    @Autowired
    @Qualifier("generalAccessor")
    private GeneralAccessor accessor = null;

    @Autowired
    private AccountManageService accountManageService = null;

    /**
     * 使用用户名、密码方式注册账户。
     *
     * @see FfeeAccountManageService#regist(String, String, String)
     */
    public FfeeAccount regist(String code, String name, String password) {
        // TODO
        return null;
    }
}

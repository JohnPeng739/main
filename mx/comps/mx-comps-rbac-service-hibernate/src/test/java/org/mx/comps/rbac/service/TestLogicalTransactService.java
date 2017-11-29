package org.mx.comps.rbac.service;

import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.dal.service.GeneralDictEntityAccessor;

/**
 * 测试逻辑事务提交和回滚的服务接口定义
 *
 * @author : john.peng created on date : 2017/11/28
 */
public interface TestLogicalTransactService extends GeneralDictEntityAccessor {
    void rollback() throws UserInterfaceRbacErrorException;

    void commit() throws UserInterfaceRbacErrorException;
}

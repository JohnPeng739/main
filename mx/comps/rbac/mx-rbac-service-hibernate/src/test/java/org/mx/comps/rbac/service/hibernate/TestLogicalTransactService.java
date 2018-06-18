package org.mx.comps.rbac.service.hibernate;

import org.mx.comps.rbac.error.UserInterfaceRbacErrorException;
import org.mx.dal.service.GeneralDictAccessor;

/**
 * 测试逻辑事务提交和回滚的服务接口定义
 *
 * @author : john.peng created on date : 2017/11/28
 */
public interface TestLogicalTransactService extends GeneralDictAccessor {
    void rollback() throws UserInterfaceRbacErrorException;

    void commit() throws UserInterfaceRbacErrorException;
}

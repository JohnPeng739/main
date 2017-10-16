package com.ds.retl.service.impl;

import com.ds.retl.dal.entity.UserOperateLog;
import com.ds.retl.exception.UserInterfaceErrorException;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.service.OperateLogService;
import org.mx.dal.EntityFactory;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.dal.service.GeneralAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


/**
 * 基于Hibernate JPA实现的用户操作日志存取服务类。
 *
 * @author : john.peng created on date : 2017/10/8
 */
@Component("operateLogService")
public class OperateLogServiceImpl implements OperateLogService {
    @Autowired
    @Qualifier("generalEntityAccessorHibernate")
    private GeneralAccessor accessor = null;

    /**
     * {@inheritDoc}
     *
     * @see OperateLogService#writeLog(String)
     */
    @Override
    public void writeLog(String conent) throws UserInterfaceErrorException {
        try {
            UserOperateLog log = EntityFactory.createEntity(UserOperateLog.class);
            log.setContent(conent);
            accessor.save(log);
        } catch (EntityInstantiationException | EntityAccessException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }
}

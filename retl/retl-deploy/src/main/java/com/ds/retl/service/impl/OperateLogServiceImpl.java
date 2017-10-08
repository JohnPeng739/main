package com.ds.retl.service.impl;

import com.ds.retl.dal.entity.UserOperateLog;
import com.ds.retl.dal.exception.UserInterfaceErrorException;
import com.ds.retl.rest.error.UserInterfaceErrors;
import org.mx.dal.EntityFactory;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.dal.service.GeneralAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by john on 2017/10/8.
 */
@Component("operateLogService")
public class OperateLogServiceImpl implements com.ds.retl.service.OperateLogService {
    @Autowired
    @Qualifier("generalEntityAccessorHibernate")
    private GeneralAccessor accessor = null;

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

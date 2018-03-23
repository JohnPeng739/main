package org.mx.dal.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.dal.EntityFactory;
import org.mx.dal.entity.OperateLog;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * 基于Hibernate JPA实现的账户操作日志存取服务类。
 *
 * @author : john.peng created on date : 2017/10/8
 */
@Component("operateLogService")
public class OperateLogServiceImpl implements OperateLogService {
    private static final Log logger = LogFactory.getLog(OperateLogServiceImpl.class);

    @Autowired
    @Qualifier("generalAccessor")
    private GeneralAccessor accessor = null;

    /**
     * {@inheritDoc}
     *
     * @see OperateLogService#writeLog(String)
     */
    @Transactional
    @Override
    public void writeLog(String conent) throws UserInterfaceDalErrorException {
        OperateLog log = EntityFactory.createEntity(OperateLog.class);
        log.setContent(conent);
        accessor.save(log);
    }
}

package org.mx.dal.service.impl;

import org.mx.dal.EntityFactory;
import org.mx.dal.entity.OperateLog;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.OperateLogService;

/**
 * 描述： 操作日志服务抽象类，定义了公共的操作方法。
 *
 * @author John.Peng
 *         Date time 2018/3/25 上午9:37
 */
public abstract class AbstractOperateLogService implements OperateLogService {
    /**
     * 写入日志
     *
     * @param system      系统
     * @param module      模块
     * @param operateType 操作类型
     * @param content     操作内容
     * @param accessor    具体化的数据库访问器
     * @throws UserInterfaceDalErrorException 操作过程中发生的异常
     */
    protected void writeLog(String system, String module, OperateLog.OperateType operateType, String content,
                            GeneralAccessor accessor) throws UserInterfaceDalErrorException {
        OperateLog log = EntityFactory.createEntity(OperateLog.class);
        log.setSystem(system);
        log.setModule(module);
        if (operateType != null) {
            log.setOperateType(operateType);
        } else {
            log.setOperateType(OperateLog.OperateType.QUERY);
        }
        log.setContent(content);
        accessor.save(log);
    }
}

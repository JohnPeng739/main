package org.mx.dal.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.entity.OperateLog;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.session.SessionDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


/**
 * 基于MongoDB实现的账户操作日志存取服务类。
 *
 * @author : john.peng created on date : 2017/10/8
 */
@Component("operateLogService")
public class OperateLogServiceImpl extends AbstractOperateLogService implements OperateLogService {
    private static final Log logger = LogFactory.getLog(AbstractOperateLogService.class);

    private GeneralAccessor accessor;
    private SessionDataStore sessionDataStore;

    /**
     * 默认的构造函数
     *
     * @param accessor         mongodb操作器
     * @param sessionDataStore 会话数据服务接口
     */
    @Autowired
    public OperateLogServiceImpl(@Qualifier("generalAccessorMongodb") GeneralAccessor accessor,
                                 SessionDataStore sessionDataStore) {
        super();
        this.accessor = accessor;
        this.sessionDataStore = sessionDataStore;
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLogService#writeLog(String)
     */
    @Override
    public void writeLog(String content) throws UserInterfaceDalErrorException {
        writeLog(null, null, null, content);
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLogService#writeLog(OperateLog.OperateType, String)
     */
    @Override
    public void writeLog(OperateLog.OperateType operateType, String content) throws UserInterfaceDalErrorException {
        writeLog(null, null, operateType, content);
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLogService#writeLog(String, String, String)
     */
    @Override
    public void writeLog(String system, String module, String content) throws UserInterfaceDalErrorException {
        writeLog(system, module, null, content);
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLogService#writeLog(String, String, OperateLog.OperateType, String)
     * @see AbstractOperateLogService#writeLog(String, String, OperateLog.OperateType, String, GeneralAccessor)
     */
    @Override
    public void writeLog(String system, String module, OperateLog.OperateType type, String content)
            throws UserInterfaceDalErrorException {
        // 如果没有设置system和module，就从sessionDataStore中获取。
        if (StringUtils.isBlank(system)) {
            system = sessionDataStore.getCurrentSystem();
        }
        if (StringUtils.isBlank(module)) {
            module = sessionDataStore.getCurrentModule();
        }
        super.writeLog(system, module, type, content, accessor);
    }
}

package org.mx.dal.service.impl;

import org.mx.StringUtils;
import org.mx.dal.entity.OperateLog;
import org.mx.dal.service.GeneralAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.dal.session.SessionDataStore;

/**
 * 描述： 基于Elastic实现的操作审计日志记录服务类
 *
 * @author John.Peng
 * Date time 2018/4/1 上午9:10
 */
public class OperateLogServiceElasticImpl extends AbstractOperateLogService implements OperateLogService {
    private GeneralAccessor accessor;
    private SessionDataStore sessionDataStore;

    /**
     * 默认的构造函数
     *
     * @param accessor         ES工具
     * @param sessionDataStore 会话数据服务接口
     */
    public OperateLogServiceElasticImpl(GeneralAccessor accessor,
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
    public void writeLog(String content) {
        writeLog(null, null, content);
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLogService#writeLog(OperateLog.OperateType, String)
     */
    @Override
    public void writeLog(OperateLog.OperateType operateType, String content) {
        writeLog(null, null, operateType, content);
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLogService#writeLog(String, String, String)
     */
    @Override
    public void writeLog(String system, String module, String content) {
        writeLog(system, module, null, content);
    }

    /**
     * {@inheritDoc}
     *
     * @see OperateLogService#writeLog(String, String, OperateLog.OperateType, String)
     * @see AbstractOperateLogService#writeLog(String, String, OperateLog.OperateType, String, GeneralAccessor)
     */
    @Override
    public void writeLog(String system, String module, OperateLog.OperateType operateType, String content) {
        // 如果没有设置system和module，就从sessionDataStore中获取。
        if (StringUtils.isBlank(system)) {
            system = sessionDataStore.getCurrentSystem();
        }
        if (StringUtils.isBlank(module)) {
            module = sessionDataStore.getCurrentModule();
        }
        super.writeLog(system, module, operateType, content, accessor);
    }
}

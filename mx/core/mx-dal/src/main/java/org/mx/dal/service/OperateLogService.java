package org.mx.dal.service;


import org.mx.dal.error.UserInterfaceDalErrorException;

/**
 * 账户操作日志存取服务接口定义
 *
 * @author : john.peng created on date : 2017/10/8
 */
public interface OperateLogService {
    /**
     * 写入操作日志数据
     *
     * @param conent 操作日志内容
     * @throws UserInterfaceDalErrorException 写入过程中发生的异常
     */
    void writeLog(String conent) throws UserInterfaceDalErrorException;
}

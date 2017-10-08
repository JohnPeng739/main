package com.ds.retl.service;

import com.ds.retl.dal.exception.UserInterfaceErrorException;

/**
 * Created by john on 2017/10/8.
 */
public interface OperateLogService {
    void writeLog(String conent) throws UserInterfaceErrorException;
}

package com.ds.retl.service;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.exception.UserInterfaceErrorException;
import com.ds.retl.rest.error.UserInterfaceErrors;

import java.util.Map;

/**
 * Created by john on 2017/11/2.
 */
public interface ServerManageService {
    enum ServiceType {SYSTEMCTL};

    interface ServiceStatus {
        boolean isEnabled();
        boolean isActived();
    }

    JSONObject getLocalServerConfigInfo() throws UserInterfaceErrorException;

    JSONObject saveLocalServerConfigInfo(String info) throws UserInterfaceErrorException;

    Map<String, JSONObject> getServerConfigInfos() throws UserInterfaceErrorException;

    void service(ServiceType type, String cmd, String service)  throws UserInterfaceErrorException;

    ServiceStatus serviceStatus(String service) throws UserInterfaceErrorException;
}

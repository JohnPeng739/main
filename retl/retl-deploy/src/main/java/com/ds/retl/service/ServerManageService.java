package com.ds.retl.service;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.exception.UserInterfaceErrorException;

import java.util.Map;

/**
 * Created by john on 2017/11/2.
 */
public interface ServerManageService {
    enum ServiceType {SYSTEMCTL};

    JSONObject getLocalServerConfigInfo() throws UserInterfaceErrorException;

    JSONObject saveLocalServerConfigInfo(String info) throws UserInterfaceErrorException;

    Map<String, JSONObject> getServerConfigInfos() throws UserInterfaceErrorException;

    void service(ServiceType type, String cmd, String service)  throws UserInterfaceErrorException;
}

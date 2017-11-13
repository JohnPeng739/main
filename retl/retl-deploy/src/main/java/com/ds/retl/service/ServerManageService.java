package com.ds.retl.service;

import com.alibaba.fastjson.JSONObject;
import com.ds.retl.exception.UserInterfaceErrorException;

import java.util.Map;

/**
 * Created by john on 2017/11/2.
 */
public interface ServerManageService {
    JSONObject getServerInfo(String machineName) throws UserInterfaceErrorException;

    ;

    JSONObject saveServerInfo(String info) throws UserInterfaceErrorException;

    JSONObject deleteServerInfo(String machineName) throws UserInterfaceErrorException;

    Map<String, JSONObject> getServerInfos() throws UserInterfaceErrorException;

    String service(ServiceType type, String cmd, String service) throws UserInterfaceErrorException;

    ServiceStatus serviceStatus(String service) throws UserInterfaceErrorException;

    enum ServiceType {SYSTEMCTL}

    public class ServiceStatus {
        private boolean enabled, active;

        public ServiceStatus() {
            super();
        }

        public ServiceStatus(boolean enabled, boolean active) {
            this();
            this.enabled = enabled;
            this.active = active;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}

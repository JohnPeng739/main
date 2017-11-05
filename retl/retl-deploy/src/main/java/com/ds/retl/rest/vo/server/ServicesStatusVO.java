package com.ds.retl.rest.vo.server;

import com.ds.retl.service.ServerManageService;

/**
 * Created by john on 2017/11/5.
 */
public class ServicesStatusVO {
    private ServerManageService.ServiceStatus zookeeperStatus;
    private ServerManageService.ServiceStatus stormStatus;

    public ServicesStatusVO(ServerManageService.ServiceStatus zookeeperStatus, ServerManageService.ServiceStatus stormStatus) {
        super();
        this.zookeeperStatus = zookeeperStatus;
        this.stormStatus = stormStatus;
    }

    public void setZookeeperStatus(ServerManageService.ServiceStatus zookeeperStatus) {
        this.zookeeperStatus = zookeeperStatus;
    }

    public void setStormStatus(ServerManageService.ServiceStatus stormStatus) {
        this.stormStatus = stormStatus;
    }

    public ServerManageService.ServiceStatus getZookeeperStatus() {
        return zookeeperStatus;
    }

    public ServerManageService.ServiceStatus getStormStatus() {
        return stormStatus;
    }
}

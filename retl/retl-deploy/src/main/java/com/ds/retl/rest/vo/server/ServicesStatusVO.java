package com.ds.retl.rest.vo.server;

import com.ds.retl.service.ServerManageService;

/**
 * Created by john on 2017/11/5.
 */
public class ServicesStatusVO {
    private ServerManageService.ServiceStatus zookeeper;
    private ServerManageService.ServiceStatus storm;

    public ServicesStatusVO(ServerManageService.ServiceStatus zookeeper, ServerManageService.ServiceStatus storm) {
        super();
        this.zookeeper = zookeeper;
        this.storm = storm;
    }

    public void setZookeeper(ServerManageService.ServiceStatus zookeeper) {
        this.zookeeper = zookeeper;
    }

    public void setStorm(ServerManageService.ServiceStatus storm) {
        this.storm = storm;
    }

    public ServerManageService.ServiceStatus getZookeeper() {
        return zookeeper;
    }

    public ServerManageService.ServiceStatus getStorm() {
        return storm;
    }
}

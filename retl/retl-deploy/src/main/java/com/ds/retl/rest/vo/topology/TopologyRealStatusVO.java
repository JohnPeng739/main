package com.ds.retl.rest.vo.topology;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by john on 2017/10/31.
 */
public class TopologyRealStatusVO {
    private String id, name, status;

    public TopologyRealStatusVO() {
        super();
    }

    public TopologyRealStatusVO(JSONObject topology) {
        this();
        this.id = topology.getString("id");
        this.name = topology.getString("name");
        this.status = topology.getString("status");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

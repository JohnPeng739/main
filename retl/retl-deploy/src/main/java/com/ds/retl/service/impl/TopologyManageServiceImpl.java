package com.ds.retl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.dal.entity.Topology;
import com.ds.retl.dal.exception.UserInterfaceErrorException;
import com.ds.retl.jms.ActiveMqJmsProvider;
import com.ds.retl.rest.error.UserInterfaceErrors;
import com.ds.retl.service.TopologyManageService;
import net.bytebuddy.asm.Advice;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.*;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.exception.EntityAccessException;
import org.mx.dal.exception.EntityInstantiationException;
import org.mx.dal.service.GeneralAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by john on 2017/10/10.
 */
@Component
public class TopologyManageServiceImpl implements TopologyManageService {
    private static final Log logger = LogFactory.getLog(TopologyManageServiceImpl.class);

    @Autowired
    @Qualifier("generalEntityAccessorHibernate")
    private GeneralAccessor accessor = null;

    @Override
    public Topology save(String name, String topologyJsonStr) throws UserInterfaceErrorException {
        try {
            Topology topology = EntityFactory.createEntity(Topology.class);
            topology.setName(name);
            topology.setSubmitted(false);
            topology.setTopologyContent(topologyJsonStr);
            return accessor.save(topology);
        } catch (EntityAccessException | EntityInstantiationException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.DB_OPERATE_FAIL);
        }
    }

    @Override
    public Topology submit(String name, String topologyJsonStr) throws UserInterfaceErrorException {
        save(name, topologyJsonStr);
        // TODO 提交计算拓扑
        return null;
    }

    @Override
    public boolean validateZookeepers(String resourceJsonStr) throws UserInterfaceErrorException {
        List<String> servers = JSON.parseObject(resourceJsonStr, List.class);
        try {
            ZooKeeper zk = new ZooKeeper(StringUtils.merge(servers, ","), 40 * 1000, null);
            zk.getState();
            zk.close();
            return true;
        } catch (IOException | InterruptedException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_VALIDATE_FAIL);
        }
    }

    @Override
    public boolean validateJdbcDataSource(String resourceJsonStr) throws UserInterfaceErrorException {
        JSONObject json = JSON.parseObject(resourceJsonStr);
        String driver = json.getString("driver"),
                url = json.getString("url"),
                user = json.getString("user"),
                password = json.getString("password");
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, user, password);
            conn.isClosed();
            conn.close();
            return true;
        } catch (ClassNotFoundException | SQLException ex) {
            throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_VALIDATE_FAIL);
        }
    }

    @Override
    public boolean validateJmsDataSource(String resourceJsonStr) throws UserInterfaceErrorException {
        JSONObject json = JSON.parseObject(resourceJsonStr);
        String protocol = json.getString("protocol"),
                server = json.getString("server"),
                user = json.getString("user"),
                password = json.getString("password"),
                method = json.getString("method");
        boolean trace = json.getBooleanValue("trace");
        String connStr = String.format("%s%s?trace=%s", protocol, server, trace ? "true" : "false");
        if ("ACTIVEMQ".equals(method)) {
            try {
                ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(user, password, connStr);
                javax.jms.Connection conn = factory.createConnection();
                conn.getClientID();
                conn.close();
                return true;
            } catch (JMSException ex) {
                throw new UserInterfaceErrorException(UserInterfaceErrors.TOPOLOGY_VALIDATE_FAIL);
            }
        } else {
            throw new UserInterfaceErrorException(UserInterfaceErrors.SYSTEM_ILLEGAL_PARAM);
        }
    }
}

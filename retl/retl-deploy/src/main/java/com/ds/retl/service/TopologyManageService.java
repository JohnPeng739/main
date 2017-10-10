package com.ds.retl.service;

import com.ds.retl.dal.entity.Topology;
import com.ds.retl.dal.exception.UserInterfaceErrorException;

/**
 * Created by john on 2017/10/10.
 */
public interface TopologyManageService {
    Topology save(String name, String topologyJsonStr) throws UserInterfaceErrorException;

    Topology submit(String name, String topologyJsonStr) throws UserInterfaceErrorException;

    boolean validateZookeepers(String resourceJsonStr) throws UserInterfaceErrorException;

    boolean validateJdbcDataSource(String resourceJsonStr) throws UserInterfaceErrorException;

    boolean validateJmsDataSource(String resourceJsonStr) throws UserInterfaceErrorException;
}

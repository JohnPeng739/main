package com.ds.retl.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ds.retl.exception.UserInterfaceErrorException;

/**
 * Storm Restful服务接口定义
 *
 * @author : john.peng created on date : 2017/10/30
 */
public interface StormRestfulServiceClient {
    /**
     * 获取Storm集群概要信息
     *
     * @return 集群概要信息
     * @throws UserInterfaceErrorException 获取过程中发生的异常
     */
    JSONObject getClusterSummary() throws UserInterfaceErrorException;

    /**
     * 获取集群中主控节点概要信息
     *
     * @return 主控节点概要信息
     * @throws UserInterfaceErrorException 获取过程中发生的异常
     */
    JSONArray getClusterNimbusSummary() throws UserInterfaceErrorException;

    /**
     * 获取集群中工作节点概要信息
     *
     * @return 工作节点概要信息
     * @throws UserInterfaceErrorException 获取过程中发生的异常
     */
    JSONArray getClusterSupervisorSummary() throws UserInterfaceErrorException;

    /**
     * 获取集群中已经部署的计算拓扑列表
     *
     * @return 计算拓扑列表
     * @throws UserInterfaceErrorException 获取过程中发生的异常
     */
    JSONArray getToptologies() throws UserInterfaceErrorException;

    /**
     * 获取集群中指定ID的计算拓扑
     *
     * @param id 计算拓扑ID
     * @return 计算拓扑对象，如果不存在，则返回null
     */
    JSONObject getTopology(String id);

    /**
     * 重新负载均衡计算拓扑
     *
     * @param topologyId 拓扑ID
     * @return 返回信息
     * @throws UserInterfaceErrorException 操作过程中发生的异常
     * @see #rebalance(String, int)
     */
    JSONObject rebalance(String topologyId) throws UserInterfaceErrorException;

    /**
     * 重新负载均衡计算拓扑
     *
     * @param topologyId 拓扑ID
     * @param waitTime   等待时间，单位为秒，如果设定小于6秒，则内部设置为6秒。
     * @return 返回信息
     * @throws UserInterfaceErrorException 操作过程中发生的异常
     */
    JSONObject rebalance(String topologyId, int waitTime) throws UserInterfaceErrorException;

    /**
     * 杀掉指定的计算拓扑
     *
     * @param topologyId 拓扑ID
     * @return 返回信息
     * @throws UserInterfaceErrorException 操作过程中发生的异常
     * @see #kill(String, int)
     */
    JSONObject kill(String topologyId) throws UserInterfaceErrorException;

    /**
     * 杀掉指定的计算拓扑
     *
     * @param topologyId 杀掉指定的计算拓扑
     * @param waitTime   等待时间，单位为秒，如果设定小于6秒，则内部设置为6秒。
     * @return 返回信息
     * @throws UserInterfaceErrorException 操作过程中发生的异常
     */
    JSONObject kill(String topologyId, int waitTime) throws UserInterfaceErrorException;
}

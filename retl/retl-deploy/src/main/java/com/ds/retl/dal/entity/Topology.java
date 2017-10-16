package com.ds.retl.dal.entity;

import org.mx.dal.entity.Base;

/**
 * 计算拓扑实体接口定义类
 *
 * @author : john.peng created on date : 2017/10/10
 */
public interface Topology extends Base {
    /**
     * 获取名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 设置名称
     *
     * @param name 名称
     */
    void setName(String name);

    /**
     * 获取是否已经提交
     *
     * @return 是否已经提交
     */
    boolean isSubmitted();

    /**
     * 设置是否已经提交
     *
     * @param submitted 是否已经提交
     */
    void setSubmitted(boolean submitted);

    /**
     * 获取提交时间
     *
     * @return 提交时间
     */
    long getSubmittedTime();

    /**
     * 设置提交时间
     *
     * @param submittedTime 提交时间
     */
    void setSubmittedTime(long submittedTime);

    /**
     * 获取提交信息
     *
     * @return 提交信息
     */
    String getSubmitInfo();

    /**
     * 设置提交信息
     *
     * @param submitInfo 提交信息
     */
    void setSubmitInfo(String submitInfo);

    /**
     * 获取拓扑配置内容
     *
     * @return 拓扑配置内容
     */
    String getTopologyContent();

    /**
     * 设置拓扑配置内容
     *
     * @param topologyContent 拓扑配置内容
     */
    void setTopologyContent(String topologyContent);
}

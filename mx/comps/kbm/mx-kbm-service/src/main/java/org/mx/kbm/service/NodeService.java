package org.mx.kbm.service;

import java.util.List;

/**
 * 描述： 知识条目服务接口定义
 *
 * @author john peng
 * Date time 2018/5/5 下午8:28
 */
public interface NodeService {
    /**
     * 对指定的知识条目进行分享操作
     *
     * @param nodeIds        知识条目ID列表
     * @param beneficiaryIds 分享收益对象ID列表
     * @param acls           分享后拥有的ACL值
     * @param startTime      分享有效开始时间
     * @param endTime        分享有效结束时间
     */
    void shareNode(List<String> nodeIds, List<String> beneficiaryIds, int acls, long startTime, long endTime);
}

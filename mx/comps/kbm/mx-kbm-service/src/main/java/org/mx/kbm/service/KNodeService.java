package org.mx.kbm.service;

import org.mx.kbm.entity.*;

import java.util.Collection;
import java.util.List;

/**
 * 描述： 知识条目服务接口定义
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午9:48
 */
public interface KNodeService {
    KNode saveKNode(KNode node);
    KNode saveKAttachment(KAttachment attachment);
    void relationship(KNode src, KNode tar, KGraph.NodeRelationship relationship);
    List<KNode> getMyKNodes(Tenant tenant);
    List<KNode> getMyKNodes(Tenant tenant, int acl);
    List<KNode> getMyKNodes(Tenant tenant, Category category);
    List<KNode> getMyKNodes(Tenant tenant, Collection<KTag> tags);
    List<KNode> getMyKNodes(Tenant tenant, Category category, int acl);
    List<KNode> getMyKNodes(Tenant tenant, int acl, Collection<KTag> tags);
    List<KNode> getMyKNodes(Tenant tenant, Category category, Collection<KTag> tags);
    List<KNode> getMyKNodes(Tenant tenant, Category category, int acl, Collection<KTag> tags);
}

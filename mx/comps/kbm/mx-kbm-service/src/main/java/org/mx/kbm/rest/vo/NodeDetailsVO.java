package org.mx.kbm.rest.vo;

import org.mx.kbm.entity.KnowledgeAcl;
import org.mx.service.rest.vo.BaseVO;

import java.util.List;

/**
 * 描述： 知识条目详细信息值对象类定义，支持同时将多个知识条目分享个多个人。
 *
 * @author John.Peng
 *         Date time 2018/5/1 上午11:20
 */
public class NodeDetailsVO extends BaseVO {
    private List<String> nodeIds;
    private String ownerId;
    private List<String> beneficiaries;
    private int acls = KnowledgeAcl.READ.getOrdinal();
    // TODO NodeDetalsVO
}

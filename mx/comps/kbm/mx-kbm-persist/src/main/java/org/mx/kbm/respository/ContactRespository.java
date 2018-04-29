package org.mx.kbm.respository;

import org.mx.kbm.entity.KnowledgeContactEntity;
import org.springframework.data.jpa.repository.Query;

/**
 * 描述： 基于Hibernate Repository实现的联系人查询接口定义
 *
 * @author John.Peng
 *         Date time 2018/4/29 下午10:59
 */
public interface ContactRespository {
    /**
     * 根据指定的联系人代码查找联系人实体对象
     *
     * @param code 联系人代码
     * @return 联系人对象
     */
    @Query("select kc from KnowledgeContactEntity kc where kc.account.code = ?1")
    KnowledgeContactEntity findByCode(String code);
}

package org.mx.kbm.respository;

import org.mx.kbm.entity.KnowledgeTenantEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * 描述： 租户查询接口定义
 *
 * @author John.Peng
 *         Date time 2018/4/30 下午12:31
 */
public interface TenantRepository extends Repository<String, KnowledgeTenantEntity> {
    /**
     * 根据指定的联系人ID，获取该联系人管理的租户对象
     *
     * @param contactId 联系人ID
     * @return 租户对象，如果没有，则返回null。
     */
    @Query("select mt from KnowledgeTenantEntity mt where mt.contact.id = ?1")
    KnowledgeTenantEntity getManagedTenantByContactId(String contactId);

    /**
     * 根据指定的联系人ID，获取该联系人所属的租户列表
     *
     * @param contactId 联系人ID
     * @return 租户对象列表
     */
    @Query("select mt from KnowledgeTenantEntity mt join KnowledgeContactEntity kc on mt.id = kc.id where kc.id = ?1")
    List<KnowledgeTenantEntity> getBelonesTenantByContactId(String contactId);
}

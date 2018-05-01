package org.mx.kbm.respository;

import org.mx.kbm.entity.KnowledgeCategory;
import org.mx.kbm.entity.KnowledgeCategoryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * 描述： 知识分类查询接口定义类
 *
 * @author John.Peng
 *         Date time 2018/5/1 上午10:10
 */
public interface CategoryRepository extends Repository<KnowledgeCategoryEntity, String> {
    /**
     * 根据指定的父级节点ID获取所有的知识分类信息实体对象
     *
     * @param parentId 父级节点ID
     * @return 知识分类信息实体对象列表
     */
    @Query("select kc from KnowledgeCategoryEntity kc where kc.parent.id = ?1")
    List<? extends KnowledgeCategory> getCategoriesByParentId(String parentId);
}

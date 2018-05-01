package org.mx.kbm.service;

import org.mx.kbm.service.bean.CategoryTreeBean;

import java.util.List;

/**
 * 描述： 知识分类信息服务接口定义
 *
 * @author John.Peng
 *         Date time 2018/5/1 上午9:42
 */
public interface CategoryService {
    /**
     * 获取指定父级节点ID的知识分类信息对象列表，包括其所有的父子关系
     *
     * @param parentId 父级节点关键字ID
     * @return 知识分类信息对象列表
     */
    List<CategoryTreeBean> getCategoryByParentId(String parentId);

    /**
     * 保存知识分类信息对象
     *
     * @param categoryTreeBean 分类信息对象
     * @return 保存成功后的分类信息对象
     */
    CategoryTreeBean saveCategory(CategoryTreeBean categoryTreeBean);
}

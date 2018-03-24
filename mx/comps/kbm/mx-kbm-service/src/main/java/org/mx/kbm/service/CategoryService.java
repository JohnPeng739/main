package org.mx.kbm.service;

import org.mx.kbm.entity.Category;
import org.mx.kbm.entity.Tenant;

import java.util.List;

/**
 * 描述： 知识分类目录服务接口定义
 *
 * @author John.Peng
 *         Date time 2018/3/24 下午9:48
 */
public interface CategoryService {
    /**
     * 获取指定租户的知识分类目录树
     * @param tenant 租户
     * @return 知识分类目录树
     */
    List<Category> getMyCategories(Tenant tenant);
    Category getCategory(String code);
    Category saveCategory(Category category, String parentCategoryId);
}

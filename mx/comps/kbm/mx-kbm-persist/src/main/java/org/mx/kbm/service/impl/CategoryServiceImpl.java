package org.mx.kbm.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.EntityFactory;
import org.mx.dal.entity.OperateLog;
import org.mx.dal.service.GeneralDictAccessor;
import org.mx.dal.service.OperateLogService;
import org.mx.kbm.entity.KnowledgeCategory;
import org.mx.kbm.entity.KnowledgeTenant;
import org.mx.kbm.error.UserInterfaceKbmErrorException;
import org.mx.kbm.respository.CategoryRepository;
import org.mx.kbm.service.CategoryService;
import org.mx.kbm.service.bean.CategoryTreeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述： 基于Hibernate JPA实现的知识分类服务类定义
 *
 * @author John.Peng
 *         Date time 2018/5/1 上午10:07
 */
@Component("categoryService")
public class CategoryServiceImpl implements CategoryService {
    private static final Log logger = LogFactory.getLog(CategoryServiceImpl.class);

    @Autowired
    @Qualifier("generalDictAccessor")
    private GeneralDictAccessor dictAccessor = null;

    @Autowired
    private CategoryRepository categoryRepository = null;

    @Autowired
    private OperateLogService operateLogService = null;

    /**
     * 采用迭代递归的方式将Entity对象转换为Bean对象
     *
     * @param entity    知识分类实体对象
     * @param cache     缓存
     * @param iteration 设置为true表示迭代递归，否则不再递归
     * @return 知识分类信息对象
     */
    @SuppressWarnings("unchecked")
    private CategoryTreeBean transform(KnowledgeCategory entity, Map<String, CategoryTreeBean> cache,
                                       boolean iteration) {
        if (cache.containsKey(entity.getId())) {
            return cache.get(entity.getId());
        }
        CategoryTreeBean bean = new CategoryTreeBean();
        bean.setId(entity.getId());
        bean.setCode(entity.getCode());
        bean.setName(entity.getName());
        bean.setDesc(entity.getDesc());
        if (entity.getParent() != null && iteration) {
            if (cache.containsKey(entity.getParent().getId())) {
                bean.setParent(cache.get(entity.getParent().getId()));
            } else {
                // 父级节点不需要继续递归
                CategoryTreeBean parent = transform((KnowledgeCategory) entity.getParent(), cache, false);
                cache.put(parent.getId(), parent);
                bean.setParent(parent);
            }
        }
        if (entity.getChildren() != null && !entity.getChildren().isEmpty() && iteration) {
            entity.getChildren().forEach(childEntity -> {
                if (childEntity != null) {
                    String childId = ((KnowledgeCategory) childEntity).getId();
                    if (cache.containsKey(childId)) {
                        bean.getChildren().add(cache.get(childId));
                    } else {
                        // 子级节点需要继续递归
                        CategoryTreeBean childBean = transform((KnowledgeCategory) childEntity, cache, true);
                        if (childBean != null) {
                            cache.put(childBean.getId(), childBean);
                            bean.getChildren().add(childBean);
                        }
                    }
                }
            });
        }
        return bean;
    }

    /**
     * {@inheritDoc}
     *
     * @see CategoryService#getCategoryByParentId(String)
     */
    @Transactional(readOnly = true)
    @Override
    public List<CategoryTreeBean> getCategoryByParentId(String parentId) {
        if (StringUtils.isBlank(parentId)) {
            parentId = null;
        }
        List<? extends KnowledgeCategory> categories = categoryRepository.getCategoriesByParentId(parentId);
        List<CategoryTreeBean> categoryTreeBeans = new ArrayList<>();
        if (categories != null && !categories.isEmpty()) {
            Map<String, CategoryTreeBean> cache = new HashMap<>();
            categories.forEach(entity -> {
                if (entity != null) {
                    CategoryTreeBean bean = transform(entity, cache, true);
                    categoryTreeBeans.add(bean);
                }
            });
        }
        return categoryTreeBeans;
    }

    /**
     * {@inheritDoc}
     *
     * @see CategoryService#saveCategory(CategoryTreeBean)
     */
    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public CategoryTreeBean saveCategory(CategoryTreeBean categoryTreeBean) {
        // 判断是否已经存在
        if (!StringUtils.isBlank(categoryTreeBean.getId())) {
            if (dictAccessor.getById(categoryTreeBean.getId(), KnowledgeCategory.class) != null) {
                throw new UserInterfaceKbmErrorException(UserInterfaceKbmErrorException.KbmErrors.CATEGORY_HAS_EXISTED);
            }
        }
        if (!StringUtils.isBlank(categoryTreeBean.getCode())) {
            if (dictAccessor.getByCode(categoryTreeBean.getCode(), KnowledgeCategory.class) != null) {
                throw new UserInterfaceKbmErrorException(UserInterfaceKbmErrorException.KbmErrors.CATEGORY_HAS_EXISTED);
            }
        }
        KnowledgeCategory parent = null;
        if (categoryTreeBean.getParent() != null) {
            // 判断父级节点是否存在
            parent = dictAccessor.getById(categoryTreeBean.getParent().getId(), KnowledgeCategory.class);
            if (parent == null) {
                throw new UserInterfaceKbmErrorException(UserInterfaceKbmErrorException.KbmErrors.CATEGORY_NOT_FOUND);
            }
        }
        KnowledgeTenant tenant = null;
        if (categoryTreeBean.getTenant() != null) {
            // 判断租户是否存在
            tenant = dictAccessor.getById(categoryTreeBean.getTenant().getId(), KnowledgeTenant.class);
            if (tenant == null) {
                throw new UserInterfaceKbmErrorException(UserInterfaceKbmErrorException.KbmErrors.TENANT_NOT_FOUND);
            }
        }
        // 保存知识分类信息
        KnowledgeCategory entity = EntityFactory.createEntity(KnowledgeCategory.class);
        entity.setCode(categoryTreeBean.getCode());
        entity.setName(categoryTreeBean.getName());
        entity.setDesc(categoryTreeBean.getDesc());
        entity.setPrivated(categoryTreeBean.isPrivated());
        entity.setParent(parent);
        entity.setTenant(tenant);
        entity = dictAccessor.save(entity);
        categoryTreeBean.setId(entity.getId());
        if (operateLogService != null) {
            operateLogService.writeLog(OperateLog.OperateType.CRUD, String.format("保存知识分类成功，代码：%s，名称：%s。",
                    categoryTreeBean.getCode(), categoryTreeBean.getName()));
        }
        return categoryTreeBean;
    }
}

package org.mx.kbm.rest.vo;

import org.mx.dal.EntityFactory;
import org.mx.kbm.entity.Category;
import org.mx.kbm.entity.Tenant;
import org.mx.service.rest.vo.BaseDictTreeVO;

/**
 * 描述： 知识分类目录VO对象定义
 *
 * @author John.Peng
 *         Date time 2018/3/26 下午2:22
 */
public class CategoryVO extends BaseDictTreeVO {
    private TenantVO tenant;

    public static CategoryVO transform(Category category) {
        if (category == null) {
            return null;
        }
        CategoryVO vo = new CategoryVO();
        BaseDictTreeVO.transform(category, vo);
        if (category.getTenant() != null) {
            TenantVO tenantVO = TenantVO.transform(category.getTenant());
            vo.setTenant(tenantVO);
        }
        return vo;
    }

    public Category transform() {
        Category category = EntityFactory.createEntity(Category.class);
        BaseDictTreeVO.transform(this, category);
        if (this.getTenant() != null) {
            Tenant tenant = this.getTenant().transform();
            category.setTenant(tenant);
        }
        return category;
    }

    public TenantVO getTenant() {
        return tenant;
    }

    public void setTenant(TenantVO tenant) {
        this.tenant = tenant;
    }
}

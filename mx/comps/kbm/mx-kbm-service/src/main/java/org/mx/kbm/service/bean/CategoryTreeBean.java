package org.mx.kbm.service.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 知识分类树信息对象类定义
 *
 * @author John.Peng
 *         Date time 2018/5/1 上午9:36
 */
public class CategoryTreeBean {
    private String id, code, name, desc;
    private TenantBean tenant;
    private boolean privated;
    private CategoryTreeBean parent;
    private List<CategoryTreeBean> children = new ArrayList<>();

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public TenantBean getTenant() {
        return tenant;
    }

    public boolean isPrivated() {
        return privated;
    }

    public CategoryTreeBean getParent() {
        return parent;
    }

    public List<CategoryTreeBean> getChildren() {
        return children;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTenant(TenantBean tenant) {
        this.tenant = tenant;
    }

    public void setPrivated(boolean privated) {
        this.privated = privated;
    }

    public void setParent(CategoryTreeBean parent) {
        this.parent = parent;
    }

    public void setChildren(List<CategoryTreeBean> children) {
        this.children = children;
    }
}

package org.mx.kbm.rest.vo;

import org.mx.kbm.entity.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 我的分类目录VO对象定义
 *
 * @author John.Peng
 *         Date time 2018/3/26 下午2:18
 */
public class MyCategoryVO {
    private List<CategoryVO> publicCategories, privateCategories;

    public MyCategoryVO() {
        super();
        this.publicCategories = new ArrayList<>();
        this.privateCategories = new ArrayList<>();
    }

    public MyCategoryVO(List<Category> publicCategories, List<Category> privateCategories) {
        this();
        this.publicCategories.clear();
        this.privateCategories.clear();
        if (publicCategories != null && !publicCategories.isEmpty()) {
            publicCategories.forEach(category -> this.publicCategories.add(CategoryVO.transform(category)));
        }
        if (privateCategories != null && !privateCategories.isEmpty()) {
            privateCategories.forEach(category -> this.privateCategories.add(CategoryVO.transform(category)));
        }
    }

    public List<CategoryVO> getPublicCategories() {
        return publicCategories;
    }

    public void setPublicCategories(List<CategoryVO> publicCategories) {
        this.publicCategories = publicCategories;
    }

    public List<CategoryVO> getPrivateCategories() {
        return privateCategories;
    }

    public void setPrivateCategories(List<CategoryVO> privateCategories) {
        this.privateCategories = privateCategories;
    }
}

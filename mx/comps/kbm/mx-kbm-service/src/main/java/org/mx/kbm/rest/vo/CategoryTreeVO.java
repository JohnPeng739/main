package org.mx.kbm.rest.vo;

import org.mx.kbm.service.bean.CategoryTreeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 知识分类树值对象类定义
 *
 * @author John.Peng
 *         Date time 2018/5/1 上午9:33
 */
public class CategoryTreeVO {
    private String id, code, name, desc;
    private List<CategoryTreeVO> children = new ArrayList<>();

    private static void transform(CategoryTreeBean bean, CategoryTreeVO vo) {
        vo.setId(bean.getId());
        vo.setCode(bean.getCode());
        vo.setName(bean.getName());
        vo.setDesc(bean.getDesc());
        List<CategoryTreeBean> childBeans = bean.getChildren();
        if (childBeans != null && !childBeans.isEmpty()) {
            childBeans.forEach(childBean -> {
                CategoryTreeVO childVO = new CategoryTreeVO();
                transform(childBean, childVO);
                vo.getChildren().add(childVO);
            });
        }
    }

    public static CategoryTreeVO transform(CategoryTreeBean bean) {
        if (bean == null) {
            return null;
        }
        CategoryTreeVO vo = new CategoryTreeVO();
        transform(bean, vo);
        return vo;
    }

    public static List<CategoryTreeVO> transform(List<CategoryTreeBean> categoryTreeBeans) {
        List<CategoryTreeVO> vos = new ArrayList<>();
        if (categoryTreeBeans != null && !categoryTreeBeans.isEmpty()) {
            categoryTreeBeans.forEach(bean -> {
                CategoryTreeVO vo = CategoryTreeVO.transform(bean);
                if (vo != null) {
                    vos.add(vo);
                }
            });
        }
        return vos;
    }

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

    public List<CategoryTreeVO> getChildren() {
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

    public void setChildren(List<CategoryTreeVO> children) {
        this.children = children;
    }
}

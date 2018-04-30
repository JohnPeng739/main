package org.mx.service.rest.vo;

import org.mx.dal.EntityFactory;
import org.mx.dal.entity.BaseDictTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 基础的树状字典数据值对象
 *
 * @author : john.peng date : 2017/10/8
 * @see BaseDictVO
 */
public class BaseDictTreeVO extends BaseDictVO {
    private BaseDictTreeVO parent;
    private List<BaseDictTreeVO> children;

    /**
     * 将树状字典对象转换为值对象
     *
     * @param baseDictTree   树状字典对象
     * @param baseDictTreeVO 值对象
     */
    @SuppressWarnings("unchecked")
    public static void transform(BaseDictTree baseDictTree, BaseDictTreeVO baseDictTreeVO) {
        if (baseDictTree == null || baseDictTreeVO == null) {
            return;
        }
        BaseDictVO.transform(baseDictTree, baseDictTreeVO);
        BaseDictTree parent = baseDictTree.getParent();
        if (parent != null) {
            BaseDictTreeVO vo = new BaseDictTreeVO();
            BaseDictTreeVO.transform(parent, vo);
            baseDictTreeVO.setParent(vo);
        }
        Set<? extends BaseDictTree> children = baseDictTree.getChildren();
        if (children != null && !children.isEmpty()) {
            List<BaseDictTreeVO> vos = new ArrayList<>(children.size());
            children.forEach(child -> {
                BaseDictTreeVO vo = new BaseDictTreeVO();
                BaseDictTreeVO.transform(child, vo);
                vos.add(vo);
            });
            baseDictTreeVO.setChildren(vos);
        }
    }

    /**
     * 将值对象转换为树状字典实体对象
     *
     * @param baseDictTreeVO 值对象
     * @param baseDictTree   树状字典实体
     */
    @SuppressWarnings("unchecked")
    public static void transform(BaseDictTreeVO baseDictTreeVO, BaseDictTree baseDictTree) {
        if (baseDictTree == null || baseDictTreeVO == null) {
            return;
        }
        BaseDictVO.transform(baseDictTreeVO, baseDictTree);
        // 仅需要设置父级节点
        BaseDictTreeVO vo = baseDictTreeVO.getParent();
        if (vo != null) {
            BaseDictTree parent = EntityFactory.createEntity(baseDictTree.getClass());
            parent.setId(vo.getId());
            baseDictTree.setParent(parent);
        }
    }

    public BaseDictTreeVO getParent() {
        return parent;
    }

    public void setParent(BaseDictTreeVO parent) {
        this.parent = parent;
    }

    public List<BaseDictTreeVO> getChildren() {
        return children;
    }

    public void setChildren(List<BaseDictTreeVO> children) {
        this.children = children;
    }
}

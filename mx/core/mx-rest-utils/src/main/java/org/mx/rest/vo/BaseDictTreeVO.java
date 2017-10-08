package org.mx.rest.vo;

import org.mx.dal.entity.BaseDict;
import org.mx.dal.entity.BaseDictTree;

/**
 * Created by john on 2017/10/8.
 */
public class BaseDictTreeVO extends BaseDictVO {
    private String parentId;

    public static void transform(BaseDictTree baseDictTree, BaseDictTreeVO baseDictTreeVO) {
        if (baseDictTree == null || baseDictTreeVO == null) {
            return;
        }
        BaseDictVO.transform(baseDictTree, baseDictTreeVO);
        baseDictTreeVO.parentId = baseDictTree.getParentId();
    }

    public static void transform(BaseDictTreeVO baseDictTreeVO, BaseDictTree baseDictTree) {
        if (baseDictTree == null || baseDictTreeVO == null) {
            return;
        }
        BaseDictVO.transform(baseDictTreeVO, baseDictTree);
        baseDictTree.setParentId(baseDictTreeVO.getParentId());
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentId() {
        return parentId;
    }
}

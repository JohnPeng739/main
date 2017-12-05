package org.mx.service.rest.vo;

import org.mx.dal.entity.BaseDictTree;

/**
 * 基础的树状字典数据值对象
 *
 * @author : john.peng date : 2017/10/8
 * @see BaseDictVO
 */
public class BaseDictTreeVO extends BaseDictVO {
    private String parentId;

    /**
     * 将树状字典对象转换为值对象
     *
     * @param baseDictTree   树状字典对象
     * @param baseDictTreeVO 值对象
     */
    public static void transform(BaseDictTree baseDictTree, BaseDictTreeVO baseDictTreeVO) {
        if (baseDictTree == null || baseDictTreeVO == null) {
            return;
        }
        BaseDictVO.transform(baseDictTree, baseDictTreeVO);
        baseDictTreeVO.parentId = baseDictTree.getParentId();
    }

    /**
     * 将值对象转换为树状字典实体对象
     *
     * @param baseDictTreeVO 值对象
     * @param baseDictTree   树状字典实体
     */
    public static void transform(BaseDictTreeVO baseDictTreeVO, BaseDictTree baseDictTree) {
        if (baseDictTree == null || baseDictTreeVO == null) {
            return;
        }
        BaseDictVO.transform(baseDictTreeVO, baseDictTree);
        baseDictTree.setParentId(baseDictTreeVO.getParentId());
    }

    /**
     * 获取父级实体关键字ID
     *
     * @return 父级实体关键字
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * 设置父级实体关键字ID
     *
     * @param parentId 父级实体关键字
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}

package org.mx.dal.entity;

/**
 * 树状字典实体接口定义
 *
 * @author : john.peng date : 2017/8/13
 * @see BaseDict
 * @see Base
 */
public interface BaseDictTree extends BaseDict {
    /**
     * 获取父级实体关键字ID
     *
     * @return 父级实体关键字
     */
    String getParentId();

    /**
     * 设置父级实体关键字ID
     *
     * @param parentId 父级实体关键字
     */
    void setParentId(String parentId);
}

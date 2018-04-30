package org.mx.dal.entity;

import java.util.Set;

/**
 * 树状字典实体接口定义
 *
 * @author : john.peng date : 2017/8/13
 * @see BaseDict
 * @see Base
 */
public interface BaseDictTree<T extends BaseDictTree> extends BaseDict {
    /**
     * 获取父级实体关键字ID
     *
     * @return 父级实体关键字
     */
    String getParentId();

    /**
     * 获取父级节点对象
     *
     * @return 父节点
     */
    T getParent();

    /**
     * 设置父级节点对象
     *
     * @param parent 父节点
     */
    void setParent(T parent);

    /**
     * 获取子级节点列表
     *
     * @return 子节点列表
     */
    Set<T> getChildren();
}

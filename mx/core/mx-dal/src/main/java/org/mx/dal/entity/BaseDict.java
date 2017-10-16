package org.mx.dal.entity;

/**
 * 基础字典实体接口定义
 *
 * @author : john.peng date : 2017/8/13
 * @see Base
 */
public interface BaseDict extends Base {
    /**
     * 获取字典代码
     *
     * @return 代码
     */
    String getCode();

    /**
     * 设置字典代码
     *
     * @param code 代码
     */
    void setCode(String code);

    /**
     * 获取名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 设置名称
     *
     * @param name 名称
     */
    void setName(String name);

    /**
     * 获取描述
     *
     * @return 描述
     */
    String getDesc();

    /**
     * 设置描述
     *
     * @param desc 描述
     */
    void setDesc(String desc);
}

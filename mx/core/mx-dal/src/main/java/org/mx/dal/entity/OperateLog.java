package org.mx.dal.entity;

/**
 * 用户操作日志实体接口定义
 *
 * @author : john.peng created on date : 2017/10/8
 * @see Base
 */
public interface OperateLog extends Base {
    /**
     * 获取系统
     *
     * @return 系统
     */
    String getSystem();

    /**
     * 设置系统
     *
     * @param system 系统
     */
    void setSystem(String system);

    /**
     * 获取模块
     *
     * @return 模块
     */
    String getModule();

    /**
     * 设置模块
     *
     * @param module 模块
     */
    void setModule(String module);

    /**
     * 获取操作类型
     *
     * @return 类型
     * @see OperateType
     */
    OperateType getOperateType();

    /**
     * 设置操作类型
     *
     * @param type 类型
     * @see OperateType
     */
    void setOperateType(OperateType type);

    /**
     * 获取用户操作信息
     *
     * @return 操作信息
     */
    String getContent();

    /**
     * 设置用户操作信息
     *
     * @param content 操作信息
     */
    void setContent(String content);

    enum OperateType {
        /**
         * 接口或API操作
         */
        API,
        /**
         * 数据访问：增删改
         */
        CRUD,
        /**
         * 数据访问：读
         */
        QUERY
    }
}

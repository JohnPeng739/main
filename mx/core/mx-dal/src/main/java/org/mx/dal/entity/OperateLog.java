package org.mx.dal.entity;

/**
 * 用户操作日志实体接口定义
 *
 * @author : john.peng created on date : 2017/10/8
 * @see Base
 */
public interface OperateLog extends Base {
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
}

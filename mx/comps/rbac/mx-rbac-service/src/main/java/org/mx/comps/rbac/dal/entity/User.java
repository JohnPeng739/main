package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.Base;

import java.util.Date;

/**
 * 用户对象定义接口
 *
 * @author : john.peng created on date : 2017/11/4
 */
public interface User extends Base {
    /**
     * 获取姓名中的名
     *
     * @return 名
     */
    String getFirstName();

    ;

    /**
     * 设置姓名中的名
     *
     * @param firstName 名
     */
    void setFirstName(String firstName);

    /**
     * 获取姓名中姓
     *
     * @return 姓
     */
    String getLastName();

    /**
     * 设置姓名中的姓
     *
     * @param lastName 姓
     */
    void setLastName(String lastName);

    /**
     * 获取姓名（全名）
     *
     * @return 姓名
     */
    String getFullName();

    /**
     * 获取用户描述
     *
     * @return 描述
     */
    String getDesc();

    /**
     * 设置用户描述
     *
     * @param desc 描述
     */
    void setDesc(String desc);

    /**
     * 获取性别
     *
     * @return 性别
     */
    Sex getSex();

    /**
     * 设置性别
     *
     * @param sex 性别
     */
    void setSex(Sex sex);

    /**
     * 获取出生日期
     *
     * @return 出生日期
     */
    Date getBirthday();

    /**
     * 获取所属部门
     *
     * @return 部门对象
     */
    Department getDepartment();

    /**
     * 设置所属部门
     *
     * @param department 部门对象
     */
    void setDepartment(Department department);

    /**
     * 获取岗位
     *
     * @return 岗位
     */
    String getStation();

    /**
     * 设置岗位
     *
     * @param station 岗位
     */
    void setStation(String station);

    /**
     * 设置出生日期
     *
     * @param birthday 出生日期
     */
    void setBirthday(Date birthday);

    /**
     * 性别枚举
     */
    enum Sex {
        /**
         * 女性
         */
        FEMALE,
        /**
         * 男性
         */
        MALE,
        /**
         * 未知
         */
        NA
    }
}

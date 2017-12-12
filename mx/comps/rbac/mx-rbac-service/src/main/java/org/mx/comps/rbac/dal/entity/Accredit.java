package org.mx.comps.rbac.dal.entity;

import org.mx.dal.entity.Base;

import java.util.Date;
import java.util.Set;

/**
 * 授权对象定义接口
 *
 * @author : john.peng created on date : 2017/11/4
 */
public interface Accredit extends Base {
    /**
     * 判断该授权是否已经失效
     *
     * @return 返回true表示该授权已经失效
     */
    boolean isClosed();

    /**
     * 获取授权源账户
     *
     * @return 账户
     */
    Account getSrc();

    /**
     * 设置授权源账户
     *
     * @param src 源账户
     */
    void setSrc(Account src);

    /**
     * 获取授权角色列表
     *
     * @return 角色列表
     */
    Set<Role> getRoles();

    /**
     * 设置授权角色列表
     *
     * @param roles 角色列表
     */
    void setRoles(Set<Role> roles);

    /**
     * 获取授权目标账户
     *
     * @return 账户
     */
    Account getTar();

    /**
     * 设置授权目标账户
     *
     * @param tar 账户
     */
    void setTar(Account tar);

    /**
     * 获取授权开始时间
     *
     * @return 开始时间
     */
    Date getStartTime();

    /**
     * 设置授权开始时间
     *
     * @param startTime 开始时间
     */
    void setStartTime(Date startTime);

    /**
     * 获取授权结束时间
     *
     * @return 结束时间
     */
    Date getEndTime();

    /**
     * 设置授权结束时间
     *
     * @param endTime 结束时间
     */
    void setEndTime(Date endTime);

    /**
     * 获取授权描述
     *
     * @return 描述
     */
    String getDesc();

    /**
     * 设置授权描述
     *
     * @param desc 描述
     */
    void setDesc(String desc);
}

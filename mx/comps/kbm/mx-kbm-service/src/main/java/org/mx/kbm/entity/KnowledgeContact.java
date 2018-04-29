package org.mx.kbm.entity;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.dal.entity.Base;

/**
 * 描述： 知识账户联系方式接口定义
 *
 * @author John.Peng
 *         Date time 2018/4/29 下午8:43
 */
public interface KnowledgeContact extends Base {
    /**
     * 获取联系人关联账户
     *
     * @return 账户
     */
    Account getAccount();

    /**
     * 设置联系人关联账户
     *
     * @param account 账户
     */
    void setAccount(Account account);

    /**
     * 获取联系人移动电话
     *
     * @return 移动电话
     */
    String getMobile();

    /**
     * 设置联系人移动电话
     *
     * @param mobile 移动电话
     */
    void setMobile(String mobile);

    /**
     * 获取联系人电子邮件
     *
     * @return 电子邮件
     */
    String getEmail();

    /**
     * 设置联系人电子邮件
     *
     * @param email 电子邮件
     */
    void setEmail(String email);

    /**
     * 获取联系人地址
     *
     * @return 地址
     */
    String getAddress();

    /**
     * 设置联系人地址
     *
     * @param address 地址
     */
    void setAddress(String address);
}

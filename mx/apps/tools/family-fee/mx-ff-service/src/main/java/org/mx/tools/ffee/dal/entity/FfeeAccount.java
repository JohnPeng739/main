package org.mx.tools.ffee.dal.entity;

import org.mx.comps.rbac.dal.entity.Account;
import org.mx.dal.entity.Base;

/**
 * 描述： FFEE中的账户信息接口定义，依赖RBAC中的Account。
 *
 * @author: John.Peng
 * @date: 2018/2/17 下午4:28
 */
public interface FfeeAccount extends Base {
    /**
     * 获取FFEE关联的常规账户对象
     *
     * @return 账户对象
     */
    Account getAccount();

    /**
     * 设置FFEE关联的常规账户对象
     *
     * @param account 账户对象
     */
    void setAccount(Account account);

    /**
     * 获取账户来源类型
     *
     * @return 账户来源类型
     */
    AccountSourceType getSourceType();

    /**
     * 设置账户来源类型
     *
     * @param sourceType 账户来源类型
     */
    void setSourceType(AccountSourceType sourceType);

    /**
     * 账户来源类型
     */
    enum AccountSourceType {
        /**
         * 常规注册
         */
        NORMAL,
        /**
         * 微信
         */
        WEIXIN,
        /**
         * 微博
         */
        WEIBO,
        /**
         * QQ
         */
        QQ
    }
}
